package org.beem.tastymap.ui.profile.health

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import org.beem.tastymap.ui.profile.health.stepScreens.DiabetesStep

class HealthWizardScreen : Screen {

    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<HealthScreenModel>()
        val state by screenModel.healthState.collectAsState()

        Scaffold(
            topBar = {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    LinearProgressIndicator(
                        progress = { (state.currentStep + 1).toFloat() / state.totalSteps },
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Text(
                        text = "Adım ${state.currentStep + 1} / ${state.totalSteps}",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp)
            ) {

                AnimatedContent(
                    targetState = state.currentStep,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "WizardStepTransition"
                ) { step ->
                    when (step) {
                       0 -> DiabetesStep(state, screenModel::toggleDiabetes)
                       // 1 -> DietStep(state, screenModel::selectEatType)
                      //  2 -> AllergyStep(state, screenModel::toggleAllergy)
                      //  3 -> SummaryStep(state, screenModel::saveHealthProfile)
                    }
                }

                // Alt navigasyon butonları (Geri ve İleri/Kaydet)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (state.currentStep > 0) {
                        OutlinedButton(onClick = { screenModel.previousStep() }) {
                            Text("Geri")
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    if (state.currentStep < state.totalSteps - 1) {
                        Button(
                            onClick = { screenModel.nextStep() },
                            // Diyet seçilmeden sonraki sayfaya geçmeyi engellemek için küçük bir validasyon
                            enabled = stepValidation(state)
                        ) {
                            Text("Sonraki")
                        }
                    }
                }
            }
        }
    }

    private fun stepValidation(state: HealthUiState): Boolean {
        return when (state.currentStep) {
            1 -> state.selectedEatType != null // Diyet seçimi zorunlu
            else -> true
        }
    }
}
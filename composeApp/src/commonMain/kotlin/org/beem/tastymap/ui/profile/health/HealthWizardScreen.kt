package org.beem.tastymap.ui.profile.health

import TastyButton
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import org.beem.tastymap.ui.components.AuthFooter
import org.beem.tastymap.ui.profile.health.stepScreens.DiabetesStep
import org.beem.tastymap.ui.profile.health.stepScreens.EatTypeStep
import org.beem.tastymap.ui.theme.LocalCustomColors // Renk yerelleştirmesi için import edildi

class HealthWizardScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<HealthScreenModel>()
        val state by screenModel.healthState.collectAsState()
        val customColors = LocalCustomColors.current

        Scaffold(
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (state.currentStep > 0) {
                        IconButton(
                            onClick = { screenModel.previousStep() },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Geri Dön",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(32.dp))
                    }

                    LinearProgressIndicator(
                        progress = { (state.currentStep + 1).toFloat() / state.totalSteps },
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp),
                        color = customColors.navy, // Progress bar rengini de palete uydurduk
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Text(
                        text = "Adım ${state.currentStep + 1} / ${state.totalSteps}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                }
            },
            bottomBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .widthIn(max = 480.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TastyButton(
                            text = "Geri",
                            onClick = { screenModel.previousStep() },
                            enabled = true,
                            isPrimary = false,
                            backcolor = Color.Transparent,
                            textcolor = customColors.navy,
                            strokecolor =  customColors.navy.copy(alpha = 0.5f) ,
                            modifier = Modifier.weight(1f)
                        )

                        TastyButton(
                            text = "Sonraki",
                            onClick = { screenModel.nextStep() },
                            enabled = stepValidation(state),
                            isPrimary = true,
                            backcolor = customColors.navy,
                            textcolor = Color.White,
                            strokecolor = customColors.navy,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    AuthFooter(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = state.currentStep,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "WizardStepTransition",
                    modifier = Modifier.fillMaxWidth()
                ) { step ->
                    when (step) {
                        0 -> DiabetesStep(state, screenModel::toggleDiabetes)
                        1 -> EatTypeStep(state, screenModel::selectEatType)
                        // 2 -> AllergyStep(state, screenModel::toggleAllergy)
                        // 3 -> SummaryStep(state, screenModel::saveHealthProfile)
                    }
                }
            }
        }
    }

    private fun stepValidation(state: HealthUiState): Boolean {
        return when (state.currentStep) {
            1 -> state.selectedEatType != null
            else -> true
        }
    }
}
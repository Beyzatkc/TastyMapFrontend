package org.beem.tastymap.ui.profile.health

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.beem.tastymap.core.util.ToastManager
import org.beem.tastymap.ui.animations.TastyAnimations
import org.beem.tastymap.ui.components.AuthFooter
import org.beem.tastymap.ui.profile.health.stepScreens.AllergiesStep
import org.beem.tastymap.ui.profile.health.stepScreens.DiabetesStep
import org.beem.tastymap.ui.profile.health.stepScreens.EatTypeStep
import org.beem.tastymap.ui.profile.health.stepScreens.SummaryStep
import org.beem.tastymap.ui.theme.CustomColors
import org.beem.tastymap.ui.theme.LocalCustomColors

class HealthWizardScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<HealthScreenModel>()
        val state by screenModel.healthState.collectAsState()
        val customColors = LocalCustomColors.current
        val navigator = LocalNavigator.currentOrThrow

        val handleBack = {
            if (state.currentStep > 0) {
                screenModel.previousStep()
            } else {
                navigator.pop()
            }
        }

        LaunchedEffect(screenModel.uiMessage) {
            screenModel.uiMessage.collect { message ->
                ToastManager.show(message)
            }
        }

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

                        IconButton(
                            onClick = { handleBack() },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Geri Dön",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                    LinearProgressIndicator(
                        progress = { (state.currentStep + 1).toFloat() / state.totalSteps },
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = customColors.gold,
                        trackColor = customColors.wave
                    )

                    Text(
                        text = "Adım ${state.currentStep + 1} / ${state.totalSteps}",
                        style = MaterialTheme.typography.labelMedium,
                        color = customColors.navy.copy(alpha = 0.7f),
                        maxLines = 1
                    )
                }
            },
            bottomBar = {
                AuthFooter(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(bottom = 10.dp)
                )
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
                    transitionSpec = {
                        if (targetState > initialState) {
                            TastyAnimations.slideInForward()
                        } else {
                            TastyAnimations.slideInBackward()
                        }
                    },
                    label = "WizardStepTransition",
                    modifier = Modifier.fillMaxWidth()
                ) { step ->
                    when (step) {
                        0 -> DiabetesStep(
                            state = state,
                            onDiabetesChanged = screenModel::toggleDiabetes,
                            onNextClick = { screenModel.nextStep() },
                            onBackClick = { handleBack() }
                        )
                        1 -> EatTypeStep(
                            state = state,
                            onEatTypeChanged = screenModel::selectEatType,
                            onNextClick = { screenModel.nextStep() },
                            onBackClick = { handleBack() }
                        )
                         2 -> AllergiesStep(
                             state = state,
                             onAllergyToggle = screenModel::toggleAllergy,
                             onNextClick = {screenModel.nextStep()},
                             onBackClick = {handleBack()}
                         )
                         3 -> SummaryStep(
                             state = state,
                             onNextClick = {screenModel.saveHealthProfile()},//ANA sayfa yonlendırmesı olcak
                             onBackClick = {handleBack()}
                         )
                    }
                }
            }
        }
    }
}
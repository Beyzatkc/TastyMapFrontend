package org.beem.tastymap.ui.profile.health.stepScreens

import androidx.compose.runtime.Composable
import org.beem.tastymap.ui.profile.health.HealthUiState
import org.beem.tastymap.ui.profile.health.components.BaseStepContainer
import org.beem.tastymap.ui.profile.health.stepScreens.common.RadioButton
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.diabetes_step_description
import tastymap.composeapp.generated.resources.diabetes_step_option_no
import tastymap.composeapp.generated.resources.diabetes_step_option_yes
import tastymap.composeapp.generated.resources.diabetes_step_title
import tastymap.composeapp.generated.resources.ic_diabetes_svg

@Composable
fun DiabetesStep(
    state: HealthUiState,
    onDiabetesChanged: (Boolean) -> Unit,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val customColors = LocalCustomColors.current

    BaseStepContainer(
        iconResource = Res.drawable.ic_diabetes_svg,
        title = stringResource(Res.string.diabetes_step_title),
        description = stringResource(Res.string.diabetes_step_description),
        onNextClick = onNextClick,
        onBackClick = onBackClick,
        isNextEnabled = true
    ) {
        RadioButton(
            text = stringResource(Res.string.diabetes_step_option_yes),
            selected = state.hasDiabetes,
            customColors = customColors
        ) {
            onDiabetesChanged(true)
        }

        RadioButton(
            text = stringResource(Res.string.diabetes_step_option_no),
            selected = !state.hasDiabetes,
            customColors = customColors
        ) {
            onDiabetesChanged(false)
        }
    }
}
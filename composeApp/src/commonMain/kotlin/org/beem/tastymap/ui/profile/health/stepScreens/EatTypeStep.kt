package org.beem.tastymap.ui.profile.health.stepScreens

import androidx.compose.runtime.Composable
import org.beem.tastymap.data.model.health.HealthEnum
import org.beem.tastymap.ui.profile.health.HealthUiState
import org.beem.tastymap.ui.profile.health.components.BaseStepContainer
import org.beem.tastymap.ui.profile.health.stepScreens.common.RadioButton
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.eat_type_description
import tastymap.composeapp.generated.resources.eat_type_normal
import tastymap.composeapp.generated.resources.eat_type_title
import tastymap.composeapp.generated.resources.eat_type_vegan
import tastymap.composeapp.generated.resources.eat_type_vegetarian
import tastymap.composeapp.generated.resources.ic_diet_svg

@Composable
fun EatTypeStep(
    state: HealthUiState,
    onEatTypeChanged: (HealthEnum) -> Unit,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val customColors = LocalCustomColors.current

    BaseStepContainer(
        iconResource = Res.drawable.ic_diet_svg,
        title = stringResource(Res.string.eat_type_title),
        description = stringResource(Res.string.eat_type_description),
        onNextClick = onNextClick,
        onBackClick = onBackClick,
        isNextEnabled = true
    ) {
        RadioButton(
            text = stringResource(Res.string.eat_type_vegetarian),
            selected = state.selectedEatType == HealthEnum.VEGETARIAN,
            customColors = customColors
        ) {
            onEatTypeChanged(HealthEnum.VEGETARIAN)
        }

        RadioButton(
            text = stringResource(Res.string.eat_type_vegan),
            selected = state.selectedEatType == HealthEnum.VEGAN,
            customColors = customColors
        ) {
            onEatTypeChanged(HealthEnum.VEGAN)
        }

        RadioButton(
            text = stringResource(Res.string.eat_type_normal),
            selected = state.selectedEatType == HealthEnum.NORMAL,
            customColors = customColors
        ) {
            onEatTypeChanged(HealthEnum.NORMAL)
        }
    }
}
package org.beem.tastymap.ui.profile.health.stepScreens

import androidx.compose.runtime.Composable
import org.beem.tastymap.data.model.health.HealthEnum
import org.beem.tastymap.ui.profile.health.HealthUiState
import org.beem.tastymap.ui.profile.health.components.BaseStepContainer
import org.beem.tastymap.ui.theme.LocalCustomColors
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.ic_diabetes_svg
import tastymap.composeapp.generated.resources.ic_diet_svg

@Composable
fun EatTypeStep (
    state: HealthUiState,
    onEatTypeChanged: (HealthEnum) -> Unit,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit
    ) {
        val customColors = LocalCustomColors.current

        BaseStepContainer(
            iconResource = Res.drawable.ic_diet_svg,
            title = "Beslenme tercihiniz nedir?",
            description = "Yemek alışkanlıklarınıza en uygun tarifleri filtreleyebilmemiz için ana beslenme tarzınızı seçin.",
            onNextClick = onNextClick,
            onBackClick = onBackClick,
            isNextEnabled = true
        ) {
            RadioButton(
                text = "Vejetaryen",
                selected = state.selectedEatType == HealthEnum.VEGETARIAN,
                customColors = customColors
            ) {
                onEatTypeChanged(HealthEnum.VEGETARIAN)
            }

            RadioButton(
                text = "Vegan",
                selected = state.selectedEatType == HealthEnum.VEGAN,
                customColors = customColors
            ) {
                onEatTypeChanged(HealthEnum.VEGAN)
            }
            RadioButton(
                text = "Genel / Kısıtlamasız",
                selected = state.selectedEatType == HealthEnum.NORMAL,
                customColors = customColors
            ) {
                onEatTypeChanged(HealthEnum.NORMAL)
            }
        }
    }
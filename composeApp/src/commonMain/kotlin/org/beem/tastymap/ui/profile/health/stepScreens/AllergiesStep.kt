package org.beem.tastymap.ui.profile.health.stepScreens
import androidx.compose.runtime.Composable
import org.beem.tastymap.ui.profile.health.HealthUiState
import org.beem.tastymap.ui.profile.health.components.BaseStepContainer
import org.beem.tastymap.ui.profile.health.stepScreens.common.CheckboxOption
import org.beem.tastymap.ui.theme.LocalCustomColors
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.ic_allergy_svg

@Composable
fun AllergiesStep(
    state: HealthUiState,
    onAllergyToggle: (Long) -> Unit,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    val customColors = LocalCustomColors.current

    BaseStepContainer(
        iconResource = Res.drawable.ic_allergy_svg,
        title = "Alerjiniz var mı?",
        description = "Sağlığınızı önemsiyoruz. Size en güvenli restoran ve menü önerilerini sunabilmemiz için alerjiniz olan gıdaları seçin.",
        onNextClick = onNextClick,
        onBackClick = onBackClick,
        isNextEnabled = true
    ){
        state.availableAllergies.forEach {allergy ->
            val isSelected = state.selectedAllergyIds.contains(allergy.id)
            CheckboxOption(
                text = allergy.name,
                checked = isSelected,
                customColors = customColors,
                onCheckedChange = {
                    onAllergyToggle(allergy.id)
                }
            )
        }

    }
}


















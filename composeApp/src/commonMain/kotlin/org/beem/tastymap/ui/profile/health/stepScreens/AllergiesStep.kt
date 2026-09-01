package org.beem.tastymap.ui.profile.health.stepScreens

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.beem.tastymap.data.model.health.AllergyInfo
import org.beem.tastymap.ui.profile.health.HealthUiState
import org.beem.tastymap.ui.profile.health.components.BaseStepContainer
import org.beem.tastymap.ui.profile.health.stepScreens.common.CheckboxOption
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.beem.tastymap.ui.theme.TastyTheme
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
    ) {
        state.availableAllergies.forEach { allergy ->
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
@Preview
@Composable
private fun AllergiesStepPreview() {
    TastyTheme(useDarkTheme = false) {
        AllergiesStep(
            state = HealthUiState(
                availableAllergies = listOf(
                    AllergyInfo(id = 1L, name = "Süt ve Süt Ürünleri"),
                    AllergyInfo(id = 2L, name = "Gluten"),
                    AllergyInfo(id = 3L, name = "Yer Fıstığı"),
                    AllergyInfo(id = 4L, name = "Yumurta"),
                    AllergyInfo(id = 5L, name = "Alerjim Yok")
                ),
                selectedAllergyIds = listOf(2L)
            ),
            onAllergyToggle = {},
            onNextClick = {},
            onBackClick = {}
        )
    }
}
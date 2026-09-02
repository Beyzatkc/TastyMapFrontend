package org.beem.tastymap.ui.profile.health.stepScreens

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.beem.tastymap.data.model.health.AllergyInfo
import org.beem.tastymap.ui.profile.health.HealthUiState
import org.beem.tastymap.ui.profile.health.components.BaseStepContainer
import org.beem.tastymap.ui.profile.health.stepScreens.common.CheckboxOption
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.beem.tastymap.ui.theme.TastyTheme
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.allergies_step_description
import tastymap.composeapp.generated.resources.allergies_step_title
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
        title = stringResource(Res.string.allergies_step_title),
        description = stringResource(Res.string.allergies_step_description),
        onNextClick = onNextClick,
        onBackClick = onBackClick,
        isNextEnabled = true
    ) {
        state.availableAllergies.forEach { allergy ->
            val isSelected = state.selectedAllergyIds.contains(allergy.id)
            CheckboxOption(
                text = stringResource(allergy.nameRes),
                checked = isSelected,
                customColors = customColors,
                onCheckedChange = {
                    onAllergyToggle(allergy.id)
                }
            )
        }
    }
}

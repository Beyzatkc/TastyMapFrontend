package org.beem.tastymap.ui.profile.health

import org.beem.tastymap.data.model.health.AllergyInfo
import org.beem.tastymap.data.model.health.HealthEnum
import org.beem.tastymap.data.model.health.HealthResponse
import org.jetbrains.compose.resources.StringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.allergy_dairy
import tastymap.composeapp.generated.resources.allergy_egg
import tastymap.composeapp.generated.resources.allergy_fish
import tastymap.composeapp.generated.resources.allergy_gluten
import tastymap.composeapp.generated.resources.allergy_none
import tastymap.composeapp.generated.resources.allergy_other
import tastymap.composeapp.generated.resources.allergy_peanut

data class HealthUiState(
    val currentStep: Int = 0,
    val isLoading: Boolean = false,

    val hasDiabetes: Boolean = false,
    val selectedEatType: HealthEnum = HealthEnum.NORMAL,
    val selectedAllergyIds: List<Long> = emptyList(),

    val initialHealthProfile: HealthResponse? = null,

    val availableAllergies: List<AllergyUiModel> = emptyList(),
    val error: String? = null,
    val isSuccess: Boolean = false
) {
    val totalSteps = 4
}

data class AllergyUiModel(
    val id: Long,
    val nameRes: StringResource
)

fun AllergyInfo.toUiModel(): AllergyUiModel {
    val res = when (this.id) {
        1L -> Res.string.allergy_dairy
        2L -> Res.string.allergy_gluten
        3L -> Res.string.allergy_peanut
        4L -> Res.string.allergy_egg
        5L -> Res.string.allergy_fish
        6L -> Res.string.allergy_none
        else -> Res.string.allergy_other
    }
    return AllergyUiModel(id = this.id, nameRes = res)
}
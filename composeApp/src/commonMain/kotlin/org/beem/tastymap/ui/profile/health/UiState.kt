package org.beem.tastymap.ui.profile.health

import org.beem.tastymap.data.model.health.AllergyInfo
import org.beem.tastymap.data.model.health.HealthEnum

data class HealthUiState(
    val currentStep: Int = 0,
    val isLoading: Boolean = false,

    val hasDiabetes: Boolean = false,
    val selectedEatType: HealthEnum = HealthEnum.NORMAL,
    val selectedAllergyIds: List<Long> = emptyList(),

    val availableAllergies: List<AllergyInfo> = emptyList(),
    val error: String? = null,
    val isSuccess: Boolean = false
) {
    val totalSteps = 4
}
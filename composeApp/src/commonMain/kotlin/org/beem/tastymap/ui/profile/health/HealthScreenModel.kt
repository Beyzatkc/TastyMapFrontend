package org.beem.tastymap.ui.profile.health
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.data.model.health.HealthEnum
import org.beem.tastymap.data.model.health.HealthRequest
import org.beem.tastymap.data.repository.HealthRepository


class HealthScreenModel(
    private val repo: HealthRepository
): ScreenModel{
    private val _healthState = MutableStateFlow(HealthUiState())
    val healthState = _healthState.asStateFlow()

    fun nextStep() {
        _healthState.update {
            if (it.currentStep < it.totalSteps - 1) {
                it.copy(currentStep = it.currentStep + 1)
            } else {
                it
            }
        }
    }
    fun previousStep() {
        _healthState.update {
            if (it.currentStep > 0) {
                it.copy(currentStep = it.currentStep - 1)
            } else {
                it
            }
        }
    }
    fun toggleDiabetes(hasDiabetes: Boolean) {
        _healthState.update { it.copy(hasDiabetes = hasDiabetes) }
    }

    fun selectEatType(eatType: HealthEnum) {
        _healthState.update { it.copy(selectedEatType = eatType) }
    }

    fun toggleAllergy(allergyId: Long) {
        _healthState.update { state ->
            val current = state.selectedAllergyIds.toMutableList()
            if (current.contains(allergyId)) current.remove(allergyId) else current.add(allergyId)
            state.copy(selectedAllergyIds = current)
        }
    }
    fun saveHealthProfile() {
        val currentState = _healthState.value
        if(currentState.isLoading)return
        screenModelScope.launch {
            _healthState.update { it.copy(isLoading = true, error = null) }
            val request = HealthRequest(
                hasDiabetes = currentState.hasDiabetes,
                eatType = currentState.selectedEatType,
                allergyIds = currentState.selectedAllergyIds
            )
            when (val result = repo.addHealth(request)) {
                is ResultWrapper.Success -> {
                    _healthState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                is ResultWrapper.Error -> {
                    _healthState.update { it.copy(isLoading = false, error = result.message) }
                }
            }
        }
    }
}
package org.beem.tastymap.ui.profile.myprofile.settings.support

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.data.model.support.SupportCategory
import org.beem.tastymap.data.repository.SupportRepository
import org.beem.tastymap.ui.auth.common.CheckValidator
import org.beem.tastymap.ui.auth.common.ValidationResult

class SupportViewModel(
    private val supportRepository: SupportRepository
) :ViewModel() {
    private val _uiState = MutableStateFlow(SupportUiState())
    val uiState = _uiState.asStateFlow()

    fun addSupport(category: SupportCategory, message: String, appVersion: String){
        if (_uiState.value.isLoading) return

        when (val validation = CheckValidator.validateSupportMessage(message)) {
            is ValidationResult.Invalid -> {
                _uiState.update { it.copy(messageError = validation.messageRes) }
                return
            }
            ValidationResult.Valid -> Unit
        }
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                )
            }
            when (val result = supportRepository.addSupport(category,message,appVersion)) {
                is ResultWrapper.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true
                        )
                    }
                }
                is ResultWrapper.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }
    fun clearMessages() {
        _uiState.update {
            it.copy(
                messageError = null,
                errorMessage = null,
                isSuccess = false
            )
        }
    }
    fun clearMessageError() {
        if (_uiState.value.messageError != null) {
            _uiState.update { it.copy(messageError = null) }
        }
    }
}
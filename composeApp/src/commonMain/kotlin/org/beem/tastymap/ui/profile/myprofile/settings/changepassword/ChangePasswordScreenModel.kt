package org.beem.tastymap.ui.profile.myprofile.settings.changepassword

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.provider.DeviceInfoProvider
import org.beem.tastymap.data.model.profile.ChangePassword
import org.beem.tastymap.data.repository.profile.MyProfileRepository
import org.beem.tastymap.ui.auth.common.CheckValidator
import org.beem.tastymap.ui.auth.common.ValidationResult

class ChangePasswordScreenModel(
    private val repo: MyProfileRepository,
    private val deviceInfoProvider: DeviceInfoProvider
) : ScreenModel {

    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState = _uiState.asStateFlow()

    fun changePassword(
        oldPassword: String,
        newPassword: String,
        againNew: String
    ) {
        _uiState.update {
            it.copy(
                oldPasswordError = null,
                newPasswordError = null,
                againNewPasswordError = null,
                errorMessage = null
            )
        }

        var hasError = false

        if (oldPassword.isBlank()) {
            _uiState.update { it.copy(oldPasswordError = "Mevcut şifrenizi giriniz.") }
            hasError = true
        }

        val passwordValidation = CheckValidator.validatePassword(newPassword.trim())
        if (passwordValidation is ValidationResult.Invalid) {
            _uiState.update { it.copy(newPasswordError = passwordValidation.message) }
            hasError = true
        }

        if (againNew.isBlank()) {
            _uiState.update { it.copy(againNewPasswordError = "Lütfen yeni şifrenizi tekrar giriniz.") }
            hasError = true
        } else if (newPassword != againNew) {
            _uiState.update { it.copy(againNewPasswordError = "Yeni şifreler birbiriyle eşleşmiyor.") }
            hasError = true
        }

        if (hasError) return

        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val request = ChangePassword(
                oldPassword = oldPassword,
                newPassword = newPassword,
                againNew = againNew,
                deviceId = deviceInfoProvider.getDeviceId()
            )

            when (val result = repo.changePassword(request)) {
                is ResultWrapper.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = result.data.message ?: "Şifreniz başarıyla değiştirildi.",
                            oldPasswordError = null,
                            newPasswordError = null,
                            againNewPasswordError = null,
                            errorMessage = null
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
                errorMessage = null,
                successMessage = null,
                oldPasswordError = null,
                newPasswordError = null,
                againNewPasswordError = null
            )
        }
    }
}
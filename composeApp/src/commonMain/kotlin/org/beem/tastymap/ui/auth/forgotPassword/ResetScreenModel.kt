package org.beem.tastymap.ui.auth.forgotPassword

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.data.model.auth.ResetPassword
import org.beem.tastymap.data.repository.UserSecurityRepository
import org.beem.tastymap.ui.auth.common.CheckValidator
import org.beem.tastymap.ui.auth.common.PasswordStrength
import org.beem.tastymap.ui.auth.common.ValidationResult
import org.jetbrains.compose.resources.StringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.change_password_error_again_empty
import tastymap.composeapp.generated.resources.change_password_error_mismatch
import tastymap.composeapp.generated.resources.verify_error_default

class ResetScreenModel(
    private val repository: UserSecurityRepository
) : ScreenModel {

    private val _uiMessage = Channel<UiMessage>()
    val uiMessage = _uiMessage.receiveAsFlow()

    private val _passwordState = MutableStateFlow(PasswordUiState())
    val passwordState = _passwordState.asStateFlow()

    sealed interface UiMessage {
        data class Dynamic(val message: String) : UiMessage
        data class Resource(val res: StringResource, val args: List<Any> = emptyList()) : UiMessage
    }

    fun resetPassword(token: String) {
        screenModelScope.launch {
            if (validatePassword()) {
                _passwordState.update { it.copy(isLoading = true) }
                val state = _passwordState.value
                val request = ResetPassword(
                    token = token,
                    newPassword = state.regPassword
                )
                when (val result = repository.resetPassword(request)) {
                    is ResultWrapper.Success -> {
                        _uiMessage.send(UiMessage.Dynamic(result.data))
                        _passwordState.update { it.copy(isChanged = true) }
                    }
                    is ResultWrapper.Error -> {
                        if (result.message != null) {
                            _uiMessage.send(UiMessage.Dynamic(result.message))
                        } else {
                            _uiMessage.send(UiMessage.Resource(Res.string.verify_error_default))
                        }
                    }
                }
                _passwordState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun validatePassword(): Boolean {
        val currentState = _passwordState.value
        val passwordResult = CheckValidator.validatePassword(currentState.regPassword.trim())
        val regPasswordError = (passwordResult as? ValidationResult.Invalid)?.messageRes

        val confirmPasswordError = when {
            currentState.confirmPassword.isBlank() -> Res.string.change_password_error_again_empty
            currentState.regPassword != currentState.confirmPassword -> Res.string.change_password_error_mismatch
            else -> null
        }

        _passwordState.update {
            it.copy(regPasswordError = regPasswordError, confirmPasswordError = confirmPasswordError)
        }
        return passwordResult is ValidationResult.Valid && confirmPasswordError == null
    }

    fun onPasswordEvent(event: PasswordEvent) {
        _passwordState.update { currentState ->
            when (event) {
                is PasswordEvent.PasswordChanged -> {
                    val newPassword = event.value
                    val strength = PasswordStrength(
                        hasMinLength = newPassword.length >= 8,
                        hasUppercase = newPassword.any { it.isUpperCase() },
                        hasDigit = newPassword.any { it.isDigit() },
                        hasSpecialChar = newPassword.contains(Regex("[@#\$!%^&*(),.?\":{}|<>]"))
                    )
                    currentState.copy(
                        regPassword = newPassword,
                        regPasswordError = null,
                        passwordStrength = strength
                    )
                }
                is PasswordEvent.ConfirmPasswordChanged -> {
                    currentState.copy(
                        confirmPassword = event.password,
                        confirmPasswordError = if (event.password == currentState.regPassword) null else currentState.confirmPasswordError
                    )
                }
            }
        }
    }

    fun backClickReset() {
        _passwordState.update {
            it.copy(
                regPassword = "",
                regPasswordError = null,
                confirmPassword = "",
                confirmPasswordError = null
            )
        }
    }
}
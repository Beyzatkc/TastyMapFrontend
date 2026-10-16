package org.beem.tastymap.ui.profile.myprofile.settings.deleteaccount

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.beem.tastymap.core.auth.AuthEventBus
import org.beem.tastymap.core.network.ErrorType
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.data.model.deleteaccount.DeleteAccountRequest
import org.beem.tastymap.data.model.deleteaccount.DeleteReason
import org.beem.tastymap.data.repository.DeleteAccountRepository

class DeleteAccountScreenModel(
    private val deleteAccountRepository: DeleteAccountRepository,
    private val authEventBus: AuthEventBus
) : ScreenModel {

    private val _uiState = MutableStateFlow(DeleteAccountUiState())
    val uiState = _uiState.asStateFlow()

    fun deleteAccount(
        password: String,
        reasonType: DeleteReason?,
        customReason: String? = null
    ) {
        screenModelScope.launch {
            _uiState.update {
                it.copy(
                    isDeleteLoading = true,
                    passwordError = null,
                )
            }

            val request = DeleteAccountRequest(
                password = password,
                reasonType = reasonType,
                customReason = customReason
            )

            when (val result = deleteAccountRepository.deleteAccount(request)) {
                is ResultWrapper.Success -> {
                    _uiState.update {
                        it.copy(
                            isDeleteLoading = false,
                            isSuccess = true
                        )
                    }
                }
                is ResultWrapper.Error -> {
                    if (result.isPasswordError()) {
                        _uiState.update {
                            it.copy(
                                isDeleteLoading = false,
                                passwordError = result.message
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isDeleteLoading = false,
                                errorMessage = result.message
                            )
                        }
                    }
                }
            }
        }
    }
    fun ResultWrapper.Error.isPasswordError(): Boolean {
        val msg = this.message?.lowercase() ?: return false
        return msg.contains("şifre") || msg.contains("password")
    }

    fun backToLogin(){
        authEventBus.emit(AuthEventBus.AuthEvent.DeleteAccount)
    }
    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null) }
    }
    fun clearPasswordError() {
        _uiState.update { it.copy(passwordError = null) }
    }
}
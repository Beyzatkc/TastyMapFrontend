package org.beem.tastymap.ui.profile.myprofile.settings.deleteaccount

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.beem.tastymap.core.auth.AuthEventBus
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
            _uiState.update { it.copy(isDeleteLoading = true, errorMessage = null) }

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
                    authEventBus.emit(AuthEventBus.AuthEvent.OnLoggedOut)
                }
                is ResultWrapper.Error -> {
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

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
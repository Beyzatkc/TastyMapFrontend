package org.beem.tastymap.ui.auth.verification

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.util.ToastManager
import org.beem.tastymap.data.repository.AuthRepository
import org.beem.tastymap.ui.auth.common.VerificationUiState

class EmailScreenModel(
    private val repository: AuthRepository,
) : ScreenModel{

    private val _verificationState = MutableStateFlow(VerificationUiState())
    val verificationState = _verificationState.asStateFlow()
    private val _timeLeft = MutableStateFlow(0)
    val timeLeft: StateFlow<Int> = _timeLeft
    private var timerJob: Job? = null


    fun startTime() {
        if (_timeLeft.value > 0) return

        timerJob?.cancel()

        timerJob = screenModelScope.launch {
            _timeLeft.value = 60

            while (_timeLeft.value > 0) {
                delay(1000)
                _timeLeft.value--
            }
        }
    }

    fun resendMail(email: String) {
        if (_verificationState.value.isLoading) return

        screenModelScope.launch {
            _verificationState.update { it.copy(isLoading = true) }

            when (val result = repository.resendEmail(email)) {
                is ResultWrapper.Success -> {
                    ToastManager.show(result.data)
                }
                is ResultWrapper.Error -> {
                    ToastManager.show(result.message ?: "Bir hata oluştu")
                }
            }
            _verificationState.update { it.copy(isLoading = false) }
        }
    }
    fun verifyEmail(token: String){
        if (_verificationState.value.isEmailVerified || _verificationState.value.isLoading) return

        screenModelScope.launch {
            _verificationState.update { it.copy(isLoading = true, verificationError = null) }
            val result = repository.verifyEmail(token)
            when(result){
                is ResultWrapper.Success -> {
                    _verificationState.update {
                        it.copy(
                            isLoading = false,
                            isEmailVerified = true
                        )
                    }
                    delay(2000)
                    ToastManager.show(result.data["message"] ?: "Başarılı")
                }
                is ResultWrapper.Error -> {
                    //ToastManager.show(result.message ?: "Bir hata oluştu")
                    _verificationState.update {
                        it.copy(
                            isLoading = false,
                            verificationError = result.message ?: "Doğrulama başarısız oldu.",
                            isEmailVerified = false
                        )
                    }
                }
            }
        }
    }

}
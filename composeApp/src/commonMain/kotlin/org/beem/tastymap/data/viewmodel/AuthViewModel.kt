package org.beem.tastymap.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.beem.tastymap.data.model.LoginRequest
import org.beem.tastymap.data.model.LoginStatus
import org.beem.tastymap.data.model.RegisterRequest
import org.beem.tastymap.data.repository.AuthRepository
import org.beem.tastymap.data.viewmodel.uistate.AuthEffect
import org.beem.tastymap.data.viewmodel.uistate.LoginResponse
import org.beem.tastymap.data.viewmodel.uistate.RegisterResponse

class AuthViewModel(private val repository: AuthRepository): ViewModel() {
    private val _uiRegisterState=MutableStateFlow(RegisterResponse())
    val uiRegisterState: StateFlow<RegisterResponse> = _uiRegisterState.asStateFlow()

    private val _uiLoginState=MutableStateFlow(LoginResponse())
    val uiLoginState: StateFlow<LoginResponse> = _uiLoginState.asStateFlow()

    private val _effect = Channel<AuthEffect>()
    val effect = _effect.receiveAsFlow()

    fun register(request: RegisterRequest){
        viewModelScope.launch {
            _uiRegisterState.update { it.copy(isLoading = true, error = null) }
            val result = repository.register(request)
            result.onSuccess { response ->
                _uiRegisterState.update { it.copy(isLoading = false, isSuccess = true, registerResponse = response) }
                _effect.send(AuthEffect.NavigateToLogin)
            }.onFailure{ error ->
                _uiRegisterState.update { it.copy(isLoading = false, error = error.message ?: "Bir hata oluştu") }
            }
        }
    }
    fun login(request: LoginRequest,userAgent:String){
        viewModelScope.launch {
            _uiLoginState.update { it.copy(isLoading = true, error = null) }
            val result = repository.login(request,userAgent)
            result.onSuccess {response ->
                if (response.status == LoginStatus.SUCCESS) {
                    _uiLoginState.update { it.copy(isLoading = false, isSuccess = true, loginResponse = response) }
                    _effect.send(AuthEffect.NavigateToHome)
                } else {
                    _uiLoginState.update { it.copy(isLoading = false, error = response.message ?: "Giriş başarısız") }
                    _effect.send(AuthEffect.NavigateToPending)
                }
            }.onFailure { error ->
                _uiLoginState.update { it.copy(isLoading = false, error = error.message ?: "Bir hata oluştu") }
            }
        }
    }
}
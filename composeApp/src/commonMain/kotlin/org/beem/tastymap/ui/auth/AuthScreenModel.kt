package org.beem.tastymap.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.beem.tastymap.core.provider.DeviceInfoProvider
import org.beem.tastymap.data.model.LoginRequest
import org.beem.tastymap.data.model.LoginStatus
import org.beem.tastymap.data.model.RegisterRequest
import org.beem.tastymap.data.repository.AuthRepository

class AuthScreenModel(
    private val repository: AuthRepository,
    private val deviceInfoProvider: DeviceInfoProvider
) : ScreenModel{
    var loginUsername by mutableStateOf("")
    var loginUsernameError by mutableStateOf<String?>(null)
    var loginPassword by mutableStateOf("")
    var logPasswordError by mutableStateOf<String?>(null)

    var regName by mutableStateOf("")
    var regnameError by mutableStateOf<String?>(null)
    var regSurname by mutableStateOf("")
    var regSurnameError by mutableStateOf<String?>(null)
    var regUsername by mutableStateOf("")
    var regusernameError by mutableStateOf<String?>(null)

    var regEmail by mutableStateOf("")
    var regEmailError by mutableStateOf<String?>(null)
    var regPassword by mutableStateOf("")
    var regPasswordError by mutableStateOf<String?>(null)

    var isLoading by mutableStateOf(false)
    var globalError by mutableStateOf<String?>(null)

    private val _effect = Channel<AuthEffect>()
    val effect = _effect.receiveAsFlow()

    fun register(){
        screenModelScope.launch {
            if (validateRegisterStep2()) {
                isLoading = true
                globalError = null

                val request = RegisterRequest(
                    username = regUsername,
                    name = regName,
                    surname = regSurname,
                    email = regEmail,
                    password = regPassword,
                    null,
                    null,
                    "USER",
                    true
                )
                val result = repository.register(request)
                result.onSuccess { response ->
                    _effect.send(AuthEffect.NavigateToLogin)
                }.onFailure { error ->
                    globalError = error.message ?: "Kayıt başarısız."
                }
                isLoading = false
            }
        }
    }
    fun login(userAgent:String){
        screenModelScope.launch {
            isLoading = true
            globalError = null

            val request = LoginRequest(
                username = loginUsername,
                password = loginPassword,
                deviceInfoProvider.getDeviceId(),
                deviceInfoProvider.getFcmToken()
            )
            val result = repository.login(request,userAgent)
            result.onSuccess {response ->
                if (response.status == LoginStatus.SUCCESS) {
                    _effect.send(AuthEffect.NavigateToHome)
                } else {
                    _effect.send(AuthEffect.NavigateToPending)
                }
            }.onFailure { error ->
                globalError = error.message ?: "Giriş başarısız."
            }
            isLoading = false
        }
    }

    fun validateRegisterStep1(): Boolean {
        val uResult = AuthValidator.validateUsername(regUsername)
        val nResult = AuthValidator.validateName(regName)
        val sResult = AuthValidator.validateSurname(regSurname)

        regusernameError = (uResult as? ValidationResult.Invalid)?.message
        regnameError = (nResult as? ValidationResult.Invalid)?.message
        regSurnameError = (sResult as? ValidationResult.Invalid)?.message

        return uResult is ValidationResult.Valid &&
                nResult is ValidationResult.Valid &&
                sResult is ValidationResult.Valid
    }
    fun validateRegisterStep2(): Boolean {
        val eResult = AuthValidator.validateEmail(regEmail)
        val pResult = AuthValidator.validatePassword(regPassword)


        regEmailError = (eResult as? ValidationResult.Invalid)?.message
        regPasswordError = (pResult as? ValidationResult.Invalid)?.message


        return eResult is ValidationResult.Valid &&
                pResult is ValidationResult.Valid
    }
    fun validateLogin(): Boolean {
        val eResult = AuthValidator.validateUsername(loginUsername)
        val pResult = AuthValidator.validatePassword(loginPassword)
        loginUsernameError = (eResult as? ValidationResult.Invalid)?.message
        logPasswordError = (pResult as? ValidationResult.Invalid)?.message
        return eResult is ValidationResult.Valid &&
                pResult is ValidationResult.Valid
    }

}
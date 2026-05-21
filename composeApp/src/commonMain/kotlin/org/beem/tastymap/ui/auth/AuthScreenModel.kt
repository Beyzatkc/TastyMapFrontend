package org.beem.tastymap.ui.auth

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.provider.DeviceInfoProvider
import org.beem.tastymap.core.util.ToastManager
import org.beem.tastymap.data.model.LoginRequest
import org.beem.tastymap.data.model.LoginStatus
import org.beem.tastymap.data.model.RegisterRequest
import org.beem.tastymap.data.repository.AuthRepository

class AuthScreenModel(
    private val repository: AuthRepository,
    private val deviceInfoProvider: DeviceInfoProvider
) : ScreenModel{

    private val _loginState = MutableStateFlow(LoginUiState())
    val loginState = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow(RegisterUiState())
    val registerState = _registerState.asStateFlow()

    private val _verificationState = MutableStateFlow(VerifiacationUiState())
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


    private val _effect = Channel<AuthEffect>()
    val effect = _effect.receiveAsFlow()


    fun nextRegisterStep() {
        if (validateRegisterStep1()) {
            _registerState.update { it.copy(step = 2) }
        }
    }

    fun previousRegisterStep() {
        _registerState.update { it.copy(step = 1) }
    }

    fun register() {
        screenModelScope.launch {
            if (validateRegisterStep2()) {
                _registerState.update { it.copy(isLoading = true) }
                val state = _registerState.value

                    val request = RegisterRequest(
                        username = state.regUsername,
                        name = state.regName,
                        surname = state.regSurname,
                        email = state.regEmail,
                        password = state.regPassword,
                        null, null, "USER", true
                    )

                    when (val result = repository.register(request)) {
                        is ResultWrapper.Success -> {
                            ToastManager.show("Kayıt başarılı!")
                            _effect.send(AuthEffect.NavigateToValidate(state.regEmail))
                        }

                        is ResultWrapper.Error -> {
                            ToastManager.show(result.message ?: "Kayıt başarısız.")
                        }
                    }
                  _registerState.update { it.copy(isLoading = false) }
                }
            }
        }

    fun login(){
        if(validateLogin()) {
            screenModelScope.launch {
                _loginState.update { it.copy(isLoading = true) }
                val currentState = _loginState.value

                val request = LoginRequest(
                    username = currentState.loginUsername,
                    password = currentState.loginPassword,
                    deviceInfoProvider.getDeviceId(),
                    deviceInfoProvider.getFcmToken()
                )
                when (val result = repository.login(request, deviceInfoProvider.getUserAgent())) {
                    is ResultWrapper.Success -> {
                        ToastManager.show("Giriş başarılı!")
                        if (result.data.status == LoginStatus.SUCCESS) {
                            _verificationState.update {it.copy(isLogin = true) }
                            _effect.send(AuthEffect.NavigateToHome)
                        } else {
                            _effect.send(AuthEffect.NavigateToPending)
                        }
                    }
                    is ResultWrapper.Error -> {
                        ToastManager.show(result.message ?: "Giriş başarısız.")
                    }
                }
                _loginState.update { it.copy(isLoading = false) }
            }
        }
    }
    fun resendMail(email: String){
        if (_registerState.value.isLoading) return
        screenModelScope.launch {
            _registerState.update { it.copy(isLoading = true) }
            when(val result = repository.resendEmail(email)){
                is ResultWrapper.Success -> {
                    ToastManager.show(result.data)
                }
                is ResultWrapper.Error -> {
                    ToastManager.show(result.message ?: "Bir hata oluştu")
                }
            }
            _registerState.update { it.copy(isLoading = false) }
        }
    }
    fun verifyEmail(token: String){
        if(_verificationState.value.isLoading){
            return
        }
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

    fun validateRegisterStep1(): Boolean {
        val currentState=_registerState.value
        val uResult = CheckValidator.validateUsername(currentState.regUsername)
        val nResult = CheckValidator.validateName(currentState.regName)
        val sResult = CheckValidator.validateSurname(currentState.regSurname)

        val usernameError = (uResult as? ValidationResult.Invalid)?.message
        val nameError = (nResult as? ValidationResult.Invalid)?.message
        val surnameError = (sResult as? ValidationResult.Invalid)?.message

        _registerState.update {
            it.copy(
                regusernameError = usernameError,
                regSurnameError = surnameError,
                regnameError = nameError
            )
        }

        return uResult is ValidationResult.Valid &&
                nResult is ValidationResult.Valid &&
                sResult is ValidationResult.Valid
    }
    fun validateRegisterStep2(): Boolean {
        val currentState=_registerState.value
        val eResult = CheckValidator.validateEmail(currentState.regEmail)
        val pResult = CheckValidator.validatePassword(currentState.regPassword)


        val regEmailError = (eResult as? ValidationResult.Invalid)?.message
        val regPasswordError = (pResult as? ValidationResult.Invalid)?.message

        _registerState.update {
            it.copy(
                regEmailError = regEmailError,
                regPasswordError = regPasswordError,
            )
        }

        return eResult is ValidationResult.Valid &&
                pResult is ValidationResult.Valid
    }
    fun validateLogin(): Boolean {
        val state = _loginState.value
        val uResult = CheckValidator.validateUsername(state.loginUsername)
        val pResult = CheckValidator.validatePassword(state.loginPassword)

        val usernameError = (uResult as? ValidationResult.Invalid)?.message
        val passwordError = (pResult as? ValidationResult.Invalid)?.message

        _loginState.update {
            it.copy(
                loginUsernameError = usernameError,
                logPasswordError = passwordError
            )
        }
        return uResult is ValidationResult.Valid && pResult is ValidationResult.Valid
    }
    fun clearRegisterForm() {
        _registerState.update {
            it.copy(
            regName = "",
            regnameError = null,
            regSurname = "",
            regSurnameError = null,
            regUsername = "",
            regusernameError = null,
            regEmail = "",
            regEmailError = null,
            regPassword = "",
            regPasswordError = null,
            )
        }
    }

    fun clearLoginForm() {
        _loginState.update {
            it.copy(
                loginUsername = "",
                loginUsernameError = null,
                loginPassword = "",
                logPasswordError = null
            )
        }
    }
    fun onRegisterEvent(event: RegisterEvent) {
        _registerState.update { currentState ->
            when (event) {
                is RegisterEvent.NameChanged ->
                    currentState.copy(regName = event.value, regnameError = null)

                is RegisterEvent.SurnameChanged ->
                    currentState.copy(regSurname = event.value, regSurnameError = null)

                is RegisterEvent.UsernameChanged ->
                    currentState.copy(regUsername = event.value, regusernameError = null)

                is RegisterEvent.EmailChanged ->
                    currentState.copy(regEmail = event.value, regEmailError = null)

                is RegisterEvent.PasswordChanged ->
                    currentState.copy(regPassword = event.value, regPasswordError = null)

            }
        }
    }

    fun onLoginEvent(event: LoginEvent) {
        _loginState.update { currentState ->
            when (event) {
                is LoginEvent.UsernameChanged ->
                    currentState.copy(loginUsername = event.value, loginUsernameError = null)
                is LoginEvent.PasswordChanged ->
                    currentState.copy(loginPassword = event.value, logPasswordError = null)
            }
        }
    }
}
sealed class LoginEvent {
    data class UsernameChanged(val value: String) : LoginEvent()
    data class PasswordChanged(val value: String) : LoginEvent()
}
sealed class RegisterEvent {
    data class NameChanged(val value: String) : RegisterEvent()
    data class SurnameChanged(val value: String) : RegisterEvent()
    data class UsernameChanged(val value: String) : RegisterEvent()

    data class EmailChanged(val value: String) : RegisterEvent()
    data class PasswordChanged(val value: String) : RegisterEvent()
}
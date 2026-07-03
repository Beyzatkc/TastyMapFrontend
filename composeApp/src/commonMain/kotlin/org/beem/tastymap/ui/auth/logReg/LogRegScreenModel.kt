package org.beem.tastymap.ui.auth.logReg
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.permission.PermissionManager
import org.beem.tastymap.core.provider.DeviceInfoProvider
import org.beem.tastymap.core.util.ToastManager
import org.beem.tastymap.data.model.LoginRequest
import org.beem.tastymap.data.model.LoginStatus
import org.beem.tastymap.data.model.RegisterRequest
import org.beem.tastymap.data.repository.AuthRepository
import org.beem.tastymap.ui.auth.common.AuthEffect
import org.beem.tastymap.ui.auth.common.CheckValidator
import org.beem.tastymap.ui.auth.common.LoginUiState
import org.beem.tastymap.ui.auth.common.PasswordStrength
import org.beem.tastymap.ui.auth.common.RegisterUiState
import org.beem.tastymap.ui.auth.common.ValidationResult

class LogRegScreenModel(
    private val repository: AuthRepository,
    private val deviceInfoProvider: DeviceInfoProvider,
    private val permissionManager: PermissionManager
) : ScreenModel{
    private val _loginState = MutableStateFlow(LoginUiState())
    val loginState = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow(RegisterUiState())
    val registerState = _registerState.asStateFlow()

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
    fun onLoginSuccess() {
        screenModelScope.launch {
            val isGranted = permissionManager.requestNotificationPermission()

            if (isGranted) {
                println("Bildirim izni verildi! İşlemlere devam edebiliriz.")
            } else {
                println("Kullanıcı izni reddetti veya bir hata oluştu.")
            }
        }
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
                val deviceId = deviceInfoProvider.getDeviceId();
                val userAgent = deviceInfoProvider.getUserAgent()
                val fcmToken = deviceInfoProvider.getFcmToken()
                val fingerPrintHash = deviceInfoProvider.getFingerprint()

                val request = LoginRequest(
                    username = currentState.loginUsername,
                    password = currentState.loginPassword,
                    deviceId,
                    fcmToken,
                    fingerPrintHash
                )
                when (val result = repository.login(request, userAgent)) {
                    is ResultWrapper.Success -> {
                        onLoginSuccess()
                        if (result.data.status == LoginStatus.SUCCESS) {
                            ToastManager.show("Giriş başarılı!")
                            _effect.send(AuthEffect.NavigateToHome)
                        } else {
                            println("LOGIN: STATUS PENDING")

                            _effect.send(AuthEffect.NavigateToPending(deviceId,fingerPrintHash))
                            println("LOGIN: NavigateToPending gönderildi")
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
    fun validateRegisterStep1(): Boolean {
        val currentState=_registerState.value
        val uResult = CheckValidator.validateUsername(currentState.regUsername.trim())
        val nResult = CheckValidator.validateName(currentState.regName.trim().replace("\\s+".toRegex(), " "))
        val sResult = CheckValidator.validateSurname(currentState.regSurname.replace("\\s+".toRegex(), " "))

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
        val eResult = CheckValidator.validateEmail(currentState.regEmail.trim())
        val pResult = CheckValidator.validatePassword(currentState.regPassword.trim())


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
        val uResult = CheckValidator.validateRequiredField(
            value = state.loginUsername.trim(),
            errorMessage = "Kullanıcı adı boş bırakılamaz"
        )
        val pResult = CheckValidator.validateRequiredField(
            value = state.loginPassword.trim(),
            errorMessage = "Şifre boş bırakılamaz"
        )

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
                passwordStrength = PasswordStrength()
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

                is RegisterEvent.PasswordChanged -> {
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

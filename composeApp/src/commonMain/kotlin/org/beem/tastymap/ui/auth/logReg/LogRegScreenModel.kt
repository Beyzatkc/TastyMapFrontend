package org.beem.tastymap.ui.auth.logReg

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.beem.tastymap.core.network.ErrorType
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.permission.PermissionManager
import org.beem.tastymap.core.provider.DeviceInfoProvider
import org.beem.tastymap.data.model.auth.CommonRequest
import org.beem.tastymap.data.model.auth.LoginRequest
import org.beem.tastymap.data.model.auth.LoginStatus
import org.beem.tastymap.data.model.auth.RegisterRequest
import org.beem.tastymap.data.repository.AuthRepository
import org.beem.tastymap.data.repository.UserSecurityRepository
import org.beem.tastymap.ui.auth.common.AuthEffect
import org.beem.tastymap.ui.auth.common.CheckValidator
import org.beem.tastymap.ui.auth.common.CountdownTimer
import org.beem.tastymap.ui.auth.common.PasswordStrength
import org.beem.tastymap.ui.auth.common.ValidationResult
import org.jetbrains.compose.resources.StringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.pending_email_sent
import tastymap.composeapp.generated.resources.validation_password_min_length
import tastymap.composeapp.generated.resources.validation_username_empty
import tastymap.composeapp.generated.resources.verify_error_default

class LogRegScreenModel(
    private val repoAuth: AuthRepository,
    private val repoSecurity: UserSecurityRepository,
    private val deviceInfoProvider: DeviceInfoProvider,
    private val permissionManager: PermissionManager
) : ScreenModel {

    private val _loginState = MutableStateFlow(LoginUiState())
    val loginState = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow(RegisterUiState())
    val registerState = _registerState.asStateFlow()

    private val _effect = Channel<AuthEffect>()
    val effect = _effect.receiveAsFlow()

    private val _uiMessage = Channel<UiMessage>()
    val uiMessage = _uiMessage.receiveAsFlow()

    val timer = CountdownTimer(screenModelScope)
    val timeLeft: StateFlow<Int> = timer.timeLeft

    sealed interface UiMessage {
        data class Dynamic(val message: String) : UiMessage
        data class Resource(val res: StringResource, val args: List<Any> = emptyList()) : UiMessage
    }

    fun startTimer() {
        timer.startTime()
    }

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
                println("Bildirim izni verildi.")
            } else {
                println("Bildirim izni reddedildi.")
            }
        }
    }

    fun register() {
        screenModelScope.launch {
            if (validateRegisterStep2()) {
                _registerState.update { it.copy(isLoading = true) }
                val state = _registerState.value
                val deviceId = deviceInfoProvider.getDeviceId()
                val request = RegisterRequest(
                    username = state.regUsername,
                    name = state.regName,
                    surname = state.regSurname,
                    email = state.regEmail,
                    password = state.regPassword,
                    null, null, "USER", true, deviceId
                )

                when (val result = repoAuth.register(request)) {
                    is ResultWrapper.Success -> {
                        _effect.send(AuthEffect.NavigateToValidate(state.regEmail, deviceId, result.data.id))
                    }

                    is ResultWrapper.Error -> {
                        if (result.message != null) {
                            _uiMessage.send(UiMessage.Dynamic(result.message))
                        } else {
                            _uiMessage.send(UiMessage.Resource(Res.string.verify_error_default))
                        }
                    }
                }
                _registerState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun login() {
        if (validateLogin()) {
            screenModelScope.launch {
                _loginState.update {
                    it.copy(
                        isLoading = true,
                        isEmailNotVerified = false,
                        unverifiedEmail = ""
                    )
                }
                val currentState = _loginState.value
                val deviceId = deviceInfoProvider.getDeviceId()
                val userAgent = deviceInfoProvider.getUserAgent()
                val fcmToken = deviceInfoProvider.getFcmToken()

                val request = LoginRequest(
                    username = currentState.loginUsername,
                    password = currentState.loginPassword,
                    deviceId,
                    fcmToken,
                )
                when (val result = repoAuth.login(request, userAgent)) {
                    is ResultWrapper.Success -> {
                        onLoginSuccess()
                        if (result.data.status == LoginStatus.SUCCESS) {
                            val isOnboardingCompleted = result.data.userResponseDTO?.onboardingCompleted ?: false
                            if (!isOnboardingCompleted) {
                                _effect.send(AuthEffect.NavigateToWelcome)
                            } else {
                                _effect.send(AuthEffect.NavigateToHome)
                            }
                        } else {
                            _effect.send(AuthEffect.NavigateToPending(deviceId))
                        }
                    }

                    is ResultWrapper.Error -> {
                        if (result.type == ErrorType.EMAIL_NOT_VERIFIED) {
                            _loginState.update {
                                it.copy(
                                    isEmailNotVerified = true,
                                    unverifiedEmail = result.email ?: ""
                                )
                            }
                        } else {
                            if (result.message != null) {
                                _uiMessage.send(UiMessage.Dynamic(result.message))
                            } else {
                                _uiMessage.send(UiMessage.Resource(Res.string.verify_error_default))
                            }
                        }
                    }
                }
                _loginState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun resendVerificationEmail() {
        val email = _loginState.value.unverifiedEmail
        if (email.isEmpty()) return
        screenModelScope.launch {
            val deviceId = deviceInfoProvider.getDeviceId()
            val request = CommonRequest(deviceId = deviceId, email = email)
            when (val result = repoSecurity.resendEmail(request)) {
                is ResultWrapper.Success -> {
                    _uiMessage.send(UiMessage.Resource(Res.string.pending_email_sent))
                    _effect.send(AuthEffect.NavigateToValidate(email, deviceId, result.data))
                    _loginState.update { it.copy(isEmailNotVerified = false) }
                }

                is ResultWrapper.Error -> {
                    if (result.message != null) {
                        _uiMessage.send(UiMessage.Dynamic(result.message))
                    } else {
                        _uiMessage.send(UiMessage.Resource(Res.string.verify_error_default))
                    }
                }
            }
        }
    }

    fun validateRegisterStep1(): Boolean {
        val currentState = _registerState.value
        val uResult = CheckValidator.validateUsername(currentState.regUsername.trim())
        val nResult = CheckValidator.validateName(currentState.regName.trim().replace("\\s+".toRegex(), " "))
        val sResult = CheckValidator.validateSurname(currentState.regSurname.replace("\\s+".toRegex(), " "))

        val usernameError = (uResult as? ValidationResult.Invalid)?.messageRes
        val nameError = (nResult as? ValidationResult.Invalid)?.messageRes
        val surnameError = (sResult as? ValidationResult.Invalid)?.messageRes

        _registerState.update {
            it.copy(
                regUsernameError = usernameError,
                regSurnameError = surnameError,
                regNameError = nameError
            )
        }

        return uResult is ValidationResult.Valid &&
                nResult is ValidationResult.Valid &&
                sResult is ValidationResult.Valid
    }

    fun validateRegisterStep2(): Boolean {
        val currentState = _registerState.value
        val eResult = CheckValidator.validateEmail(currentState.regEmail.trim())
        val pResult = CheckValidator.validatePassword(currentState.regPassword.trim())

        val regEmailError = (eResult as? ValidationResult.Invalid)?.messageRes
        val regPasswordError = (pResult as? ValidationResult.Invalid)?.messageRes

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
            errorRes = Res.string.validation_username_empty
        )
        val pResult = CheckValidator.validateRequiredField(
            value = state.loginPassword.trim(),
            errorRes = Res.string.validation_password_min_length
        )

        val usernameError = (uResult as? ValidationResult.Invalid)?.messageRes
        val passwordError = (pResult as? ValidationResult.Invalid)?.messageRes
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
                step = 1,
                regName = "",
                regNameError = null,
                regSurname = "",
                regSurnameError = null,
                regUsername = "",
                regUsernameError = null,
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
                    currentState.copy(regName = event.value, regNameError = null)

                is RegisterEvent.SurnameChanged ->
                    currentState.copy(regSurname = event.value, regSurnameError = null)

                is RegisterEvent.UsernameChanged ->
                    currentState.copy(regUsername = event.value, regUsernameError = null)

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
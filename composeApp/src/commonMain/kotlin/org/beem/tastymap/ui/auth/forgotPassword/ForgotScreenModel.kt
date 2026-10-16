package org.beem.tastymap.ui.auth.forgotPassword

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.provider.DeviceInfoProvider
import org.beem.tastymap.data.model.auth.PasswordRequest
import org.beem.tastymap.data.model.domain.SecurityEventType
import org.beem.tastymap.data.remote.AuthWebSocketClient
import org.beem.tastymap.data.repository.UserSecurityRepository
import org.beem.tastymap.ui.auth.common.AuthLifecycleEvent
import org.beem.tastymap.ui.auth.common.CheckValidator
import org.beem.tastymap.ui.auth.common.ValidationResult
import org.jetbrains.compose.resources.StringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.change_password_success
import tastymap.composeapp.generated.resources.pending_email_sent
import tastymap.composeapp.generated.resources.verify_error_default
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi

@OptIn(ExperimentalAtomicApi::class)
class ForgotScreenModel(
    private val repoSecurity: UserSecurityRepository,
    private val deviceInfoProvider: DeviceInfoProvider,
    private val authWebSocketClient: AuthWebSocketClient,
    private val resetSession: PasswordResetSessionManager
) : ScreenModel {

    private val _navigationState = Channel<PasswordNavEffect>()
    val navigationState = _navigationState.receiveAsFlow()

    private val _uiMessage = Channel<UiMessage>()
    val uiMessage = _uiMessage.receiveAsFlow()

    private val _sendState = MutableStateFlow(PasswordEmailState())
    val sendState = _sendState.asStateFlow()

    private var lifecycleJob: Job? = null
    private var isLifecycleStopped = false
    private var wasBackgrounded = false
    private var webSocketJob: Job? = null
    private val isConnecting = AtomicBoolean(false)

    sealed interface UiMessage {
        data class Dynamic(val message: String) : UiMessage
        data class Resource(val res: StringResource, val args: List<Any> = emptyList()) : UiMessage
    }

    enum class PasswordNavEffect {
        OnSuccess
    }

    fun handleLifecycleEvent(event: AuthLifecycleEvent) {
        println("FORGOT_LOG MODEL EVENT: $event")
        when (event) {
            AuthLifecycleEvent.Resume -> {
                isLifecycleStopped = false

                if (lifecycleJob?.isActive == true || isConnecting.load() || webSocketJob?.isActive == true) {
                    return
                }

                lifecycleJob = screenModelScope.launch {
                    val context = resetSession.get()
                    if (context != null) {
                        println("FORGOT_LOG: API kontrolü başlatılıyor -> ${context.deviceId}")

                        try {
                            val changed = isPasswordChanged(context.userId)
                            if (changed) return@launch
                        } catch (e: Exception) {
                            println("FORGOT_LOG: API sorgusu sırasında hata oluştu: ${e.message}")
                        }

                        startWebSocket(context.deviceId)
                    }
                }
            }
            AuthLifecycleEvent.Stop -> {
                println("FORGOT_LOG: STOP algılandı. Temizlik yapılıyor.")
                isLifecycleStopped = true
                wasBackgrounded = true
                lifecycleJob?.cancel()
                stopWebSocket()
            }
            AuthLifecycleEvent.Pause -> Unit
        }
    }

    private suspend fun isPasswordChanged(userId: Long): Boolean {
        return when (val result = repoSecurity.isPasswordUsed(userId)) {
            is ResultWrapper.Success -> {
                if (result.data) {
                    clearResetContext()
                    _uiMessage.send(UiMessage.Resource(Res.string.change_password_success))
                    _navigationState.send(PasswordNavEffect.OnSuccess)
                    true
                } else {
                    false
                }
            }
            is ResultWrapper.Error -> {
                if (result.message != null) {
                    _uiMessage.send(UiMessage.Dynamic(result.message))
                } else {
                    _uiMessage.send(UiMessage.Resource(Res.string.verify_error_default))
                }
                false
            }
        }
    }

    private fun startWebSocket(deviceId: String) {
        if (!isConnecting.compareAndSet(false, true)) return
        webSocketJob?.cancel()

        webSocketJob = screenModelScope.launch {
            val eventJob = launch { collectEvents() }

            try {
                while (isActive && !isLifecycleStopped) {
                    try {
                        if (wasBackgrounded) {
                            val context = resetSession.get()
                            if (context != null && isPasswordChanged(context.userId)) {
                                wasBackgrounded = false
                                break
                            }
                            wasBackgrounded = false
                        }
                        authWebSocketClient.connect(deviceId)
                    } catch (e: Exception) {
                        println("WS Reconnect Error: ${e.message}")
                    }

                    if (!isActive || isLifecycleStopped) break
                    delay(5000)
                }
            } finally {
                eventJob.cancel()
                isConnecting.store(false)
            }
        }
    }

    private suspend fun collectEvents() {
        authWebSocketClient.events.collect {
            when (it.type) {
                SecurityEventType.PASSWORD_CHANGE -> {
                    clearResetContext()
                    stopWebSocket()
                    _navigationState.send(PasswordNavEffect.OnSuccess)
                }
                else -> Unit
            }
        }
    }

    fun stopWebSocket() {
        isConnecting.store(false)
        webSocketJob?.cancel()
        webSocketJob = null

        screenModelScope.launch {
            authWebSocketClient.disconnect()
        }
    }

    private fun clearResetContext() {
        resetSession.clear()
    }

    fun forgotPassword(identifier: String) {
        if (_sendState.value.isLoading) return
        screenModelScope.launch {
            if (validateEmail()) {
                _sendState.update { it.copy(isLoading = true) }
                val dto = PasswordRequest(
                    deviceId = deviceInfoProvider.getDeviceId(),
                    identifier = identifier
                )
                when (val result = repoSecurity.forgotPassword(dto)) {
                    is ResultWrapper.Success -> {
                        resetSession.clear()
                        resetSession.save(result.data.userId, result.data.deviceId)
                        if (result.data.message != null) {
                            _uiMessage.send(UiMessage.Dynamic(result.data.message))
                        } else {
                            _uiMessage.send(UiMessage.Resource(Res.string.pending_email_sent))
                        }
                        startWebSocket(result.data.deviceId)
                    }

                    is ResultWrapper.Error -> {
                        if (result.message != null) {
                            _uiMessage.send(UiMessage.Dynamic(result.message))
                        } else {
                            _uiMessage.send(UiMessage.Resource(Res.string.verify_error_default))
                        }
                    }
                }
                _sendState.update { it.copy(isLoading = false, pasEmail = "", pasEmailError = null) }
            }
        }
    }

    fun onBackClickForgot() {
        _sendState.update {
            it.copy(
                pasEmail = "",
                pasEmailError = null
            )
        }
    }

    fun validateEmail(): Boolean {
        val state = _sendState.value
        val eResult = CheckValidator.validateEmail(
            email = state.pasEmail
        )
        val eError = (eResult as? ValidationResult.Invalid)?.messageRes
        _sendState.update {
            it.copy(
                pasEmailError = eError
            )
        }
        return eResult is ValidationResult.Valid
    }

    fun onEmailEvent(event: EmailEvent) {
        _sendState.update { currentState ->
            when (event) {
                is EmailEvent.EmailChanged -> {
                    currentState.copy(
                        pasEmail = event.value,
                        pasEmailError = null
                    )
                }
            }
        }
    }
}

sealed class PasswordEvent {
    data class PasswordChanged(val value: String) : PasswordEvent()
    data class ConfirmPasswordChanged(val password: String) : PasswordEvent()
}

sealed class EmailEvent {
    data class EmailChanged(val value: String) : EmailEvent()
}
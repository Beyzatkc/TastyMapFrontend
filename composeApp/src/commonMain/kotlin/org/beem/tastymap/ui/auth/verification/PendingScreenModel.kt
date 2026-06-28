package org.beem.tastymap.ui.auth.verification
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
import org.beem.tastymap.core.util.ToastManager
import org.beem.tastymap.data.model.ApprovedRefreshRequestDTO
import org.beem.tastymap.data.model.domain.SecurityEventType
import org.beem.tastymap.data.remote.AuthWebSocketClient
import org.beem.tastymap.data.repository.AuthRepository
import org.beem.tastymap.ui.auth.common.AuthEffect
import org.beem.tastymap.ui.auth.common.AuthLifecycleEvent
import org.beem.tastymap.ui.auth.common.sendPendingMail


class PendingScreenModel(
    private val repository: AuthRepository,
    private val authWebSocketClient: AuthWebSocketClient,
) : ScreenModel {
    private val _pendingLogin = Channel<AuthEffect>()
    val pendingLogin = _pendingLogin.receiveAsFlow()

    private val _sendState = MutableStateFlow(sendPendingMail())
    val sendState = _sendState.asStateFlow()

    private var isConnected = false

    private val _timeLeft = MutableStateFlow(0)
    val timeLeft: StateFlow<Int> = _timeLeft
    private var timerJob: Job? = null

    private var webSocketJob: Job? = null

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
    fun resendEmail(deviceId: String){
        if(_sendState.value.isLoading) return

        screenModelScope.launch {
            _sendState.update { it.copy(isLoading = true) }

            when(val result = repository.resendSecurityMail(deviceId)){
                is ResultWrapper.Success -> {
                    ToastManager.show(result.data ?: "Bir hata oluştu")
                }
                is ResultWrapper.Error -> {
                    ToastManager.show(result.message)
                }
            }
            _sendState.update { it.copy(isLoading = false) }
        }
    }

    fun onLifecycleEvent(event: AuthLifecycleEvent, dto: ApprovedRefreshRequestDTO) {
        when (event) {
            AuthLifecycleEvent.Resume -> {
                startWebSocket(dto)
            }

            AuthLifecycleEvent.Stop -> screenModelScope.launch {
                stopWebSocket()
            }

            AuthLifecycleEvent.Pause -> {}
        }
    }

    fun startWebSocket(approvedRefreshRequestDTO: ApprovedRefreshRequestDTO) {
        if (isConnected) return
        isConnected = true

        webSocketJob = screenModelScope.launch {
            launch {
                authWebSocketClient.events.collect { event ->
                    when (event.type) {
                        SecurityEventType.LOGIN_APPROVED -> {
                            when (val result = repository.verifyLogin(approvedRefreshRequestDTO)) {
                                is ResultWrapper.Success -> {
                                    _pendingLogin.send(AuthEffect.NavigateToHome)
                                    ToastManager.show("Giriş onaylandı.")
                                    stopWebSocket()
                                }
                                is ResultWrapper.Error -> {
                                    ToastManager.show(result.message ?: "Giriş başarısız.")
                                    _pendingLogin.send(AuthEffect.NavigateToLogin)
                                    stopWebSocket()
                                }
                            }
                        }
                        SecurityEventType.LOGIN_REJECTED -> {
                            stopWebSocket()
                            ToastManager.show("Giriş isteği reddedildi.")
                            _pendingLogin.send(AuthEffect.NavigateToLogin)
                        }
                    }
                }
            }

            try {
                authWebSocketClient.connect(approvedRefreshRequestDTO.deviceId)
            } catch (e: Exception) {
                println("LIFECYCLE: websocket hatası: " + e)
            } finally {
                isConnected = false
            }
        }
    }

    fun stopWebSocket() {
        screenModelScope.launch {
            authWebSocketClient.disconnect()
            webSocketJob?.cancel()
            isConnected = false
        }
    }
}


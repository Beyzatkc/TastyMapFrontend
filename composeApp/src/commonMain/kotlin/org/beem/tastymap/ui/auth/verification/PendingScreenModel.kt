package org.beem.tastymap.ui.auth.verification
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.provider.DeviceInfoProvider
import org.beem.tastymap.core.util.ToastManager
import org.beem.tastymap.data.model.ApprovedRefreshRequestDTO
import org.beem.tastymap.data.model.Status
import org.beem.tastymap.data.model.domain.SecurityEventType
import org.beem.tastymap.data.remote.AuthWebSocketClient
import org.beem.tastymap.data.repository.AuthRepository
import org.beem.tastymap.ui.auth.common.AuthEffect
import org.beem.tastymap.ui.auth.common.AuthLifecycleEvent
import org.beem.tastymap.ui.auth.common.CountdownTimer
import org.beem.tastymap.ui.auth.common.RequestState
import kotlin.Boolean
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi


class PendingScreenModel(
    private val repository: AuthRepository,
    private val authWebSocketClient: AuthWebSocketClient,
    private val deviceInfoProvider: DeviceInfoProvider
) : ScreenModel {

    val timer = CountdownTimer(screenModelScope)

    val timeLeft: StateFlow<Int> = timer.timeLeft

    fun startTimer() {
        timer.startTime()
    }
    companion object {
        private const val RECONNECT_DELAY = 5000L
    }

    enum class ConnectionState {
        DISCONNECTED,
        CONNECTING,
        STOPPED
    }

    @OptIn(ExperimentalAtomicApi::class)
    private val isConnecting = AtomicBoolean(false)

    private val _sendState = MutableStateFlow(RequestState())
    val sendState = _sendState.asStateFlow()

    private var wasBackgrounded = false



    private var webSocketJob: Job? = null
    private var connectionState = ConnectionState.DISCONNECTED

    private val _pendingLogin = Channel<AuthEffect>()
    val pendingLogin = _pendingLogin.receiveAsFlow()


    private val _uiMessage = Channel<String>()
    val uiMessage = _uiMessage.receiveAsFlow()



    private suspend fun createRefreshRequest(
        deviceId: String
    ): ApprovedRefreshRequestDTO {

        return ApprovedRefreshRequestDTO(
            deviceId = deviceId,
            userAgent = deviceInfoProvider.getUserAgent(),
            fcmToken = deviceInfoProvider.getFcmToken()
        )
    }



    fun onLifecycleEvent(
        event: AuthLifecycleEvent,
        deviceId: String
    ) {
        when (event) {
            AuthLifecycleEvent.Resume -> {
                connectionState = ConnectionState.CONNECTING
                screenModelScope.launch {
                    val dto = createRefreshRequest(deviceId)
                    if (wasBackgrounded) {
                        wasBackgrounded = false
                        val handled = isUsedNotification(dto)

                        if (!handled) {
                            startWebSocket(dto)
                        }
                    } else {
                        startWebSocket(dto)
                    }
                }
            }
            AuthLifecycleEvent.Stop -> {
                wasBackgrounded = true
                stopWebSocket()
            }
            AuthLifecycleEvent.Pause -> Unit
        }
    }

    fun resendEmail(deviceId: String) {
        if (_sendState.value.isLoading) return

        screenModelScope.launch {
            _sendState.update { it.copy(isLoading = true) }

            when (val result = repository.resendSecurityMail(deviceId)) {
                is ResultWrapper.Success -> {
                    _uiMessage.send(result.data ?: "E-posta gönderildi.")
                }
                is ResultWrapper.Error -> {
                    _uiMessage.send(result.message)
                }
            }
            _sendState.update { it.copy(isLoading = false) }
        }
    }


    private suspend fun isUsedNotification(
        dto: ApprovedRefreshRequestDTO
    ): Boolean {
        return when (val result = repository.isUsedNotification(dto.deviceId)) {
            is ResultWrapper.Success -> {
                println("succese1 girdi")
                println("status=${result.data.status} used=${result.data.isUsed}")
                if (result.data.status == Status.APPROVED && result.data.isUsed == true) {
                    println("verifyLogin çağrılıyor")
                    when (val verifyResult =repository.verifyLogin(dto)) {
                        is ResultWrapper.Success -> {
                            _uiMessage.send("Giriş onaylandı.")
                            _pendingLogin.send(AuthEffect.NavigateToHome)
                        }
                        is ResultWrapper.Error -> {
                            _uiMessage.send(verifyResult.message)
                            _pendingLogin.send(AuthEffect.NavigateToLogin)
                        }
                    }
                    stopWebSocket()
                    true
                }
                else if (result.data.status == Status.REJECTED) {
                    _uiMessage.send("Giriş isteği reddedildi.")
                    _pendingLogin.send(AuthEffect.NavigateToLogin)
                    stopWebSocket()
                    true
                }
                else {
                    false
                }
            }
            is ResultWrapper.Error ->{
                _uiMessage.send(result.message)
                println("Bildirim durumu kontrol edilirken hata: ${result.message}")
                false
            }
        }
    }

    @OptIn(ExperimentalAtomicApi::class)
    private fun startWebSocket(dto: ApprovedRefreshRequestDTO) {

        if (!isConnecting.compareAndSet(false, true))
            return
        webSocketJob?.cancel()
        webSocketJob = screenModelScope.launch {

            val eventJob = launch {

                collectEvents(dto)
            }
            try{

                while (isActive && connectionState != ConnectionState.STOPPED) {

                    try {
                        authWebSocketClient.connect(dto.deviceId)
                    } catch (e: Exception) {
                        println(e)
                    }
                    if (!isActive || connectionState == ConnectionState.STOPPED) break
                    delay(RECONNECT_DELAY)
                }
            }finally{
                eventJob.cancelAndJoin()
                isConnecting.store(false)
                if (connectionState != ConnectionState.STOPPED) {
                    connectionState = ConnectionState.DISCONNECTED
                }
            }
        }
    }

    private suspend fun collectEvents(
        dto: ApprovedRefreshRequestDTO
    ) {
        authWebSocketClient.events.collect { event ->
            when (event.type) {
                SecurityEventType.LOGIN_APPROVED -> {
                    handleApproved(dto)
                }
                SecurityEventType.LOGIN_REJECTED -> {
                    handleRejected()
                }
            }
        }
    }
    private suspend fun handleApproved(
        dto: ApprovedRefreshRequestDTO
    ) {
        when (val result = repository.verifyLogin(dto)) {
            is ResultWrapper.Success -> {
                _uiMessage.send("Giriş onaylandı.")
                _pendingLogin.send(AuthEffect.NavigateToHome)
                stopWebSocket()
            }

            is ResultWrapper.Error -> {
                _uiMessage.send(result.message ?: "Giriş başarısız.")
                _pendingLogin.send(AuthEffect.NavigateToLogin)
                stopWebSocket()
            }

        }
    }

    private suspend fun handleRejected() {
        stopWebSocket()
        _uiMessage.send("Giriş isteği reddedildi.")
        _pendingLogin.send(AuthEffect.NavigateToLogin)

    }

    @OptIn(ExperimentalAtomicApi::class)
    fun stopWebSocket() {
        connectionState = ConnectionState.STOPPED
        isConnecting.store(false)

        webSocketJob?.cancel()

        screenModelScope.launch {
            try {
                authWebSocketClient.disconnect()
            } catch (e: Exception) {
                println("Disconnect hatası: $e")
            }
        }
    }

}


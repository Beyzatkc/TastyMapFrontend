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
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.util.ToastManager
import org.beem.tastymap.data.model.ApprovedRefreshRequestDTO
import org.beem.tastymap.data.model.CommonRequest
import org.beem.tastymap.data.model.Status
import org.beem.tastymap.data.model.domain.SecurityEventType
import org.beem.tastymap.data.remote.AuthWebSocketClient
import org.beem.tastymap.data.repository.AuthRepository
import org.beem.tastymap.ui.auth.common.AuthEffect
import org.beem.tastymap.ui.auth.common.AuthLifecycleEvent
import org.beem.tastymap.ui.auth.common.CountdownTimer
import org.beem.tastymap.ui.auth.common.VerificationUiState
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi
@OptIn(ExperimentalAtomicApi::class)
class EmailScreenModel(
    private val repository: AuthRepository,
    private val authWebSocketClient: AuthWebSocketClient
) : ScreenModel{
    val timer = CountdownTimer(screenModelScope)
    val timeLeft: StateFlow<Int> = timer.timeLeft
    fun startTimer() {
        timer.startTime()
    }
    private val _verificationState = MutableStateFlow(VerificationUiState())
    val verificationState = _verificationState.asStateFlow()

    private val _navigationState = Channel<EmailNavEffect>()
    val navigationState = _navigationState.receiveAsFlow()

    private val _uiMessage = Channel<String>()

    val uiMessage = _uiMessage.receiveAsFlow()

    private var verificationContext: VerificationContext? = null

    data class VerificationContext(
        val userId: Long,
        val deviceId: String
    )

    private var lifecycleJob: Job? = null

    private var lifecycleStopped = false
    private var wasBackgrounded = false
    private var webSocketJob: Job? = null
    private val isConnecting = AtomicBoolean(false)
    enum class EmailNavEffect { OnSuccess }

    fun setVerificationContext(userId: Long, deviceId: String){
        verificationContext = VerificationContext(userId, deviceId)
    }

    fun resendMail(deviceId:String, email:String){
        if(_verificationState.value.isLoading)
            return
        screenModelScope.launch {
            _verificationState.update { it.copy(
                    isLoading = true)
            }

            val request =CommonRequest(deviceId = deviceId, email = email)
            when(val result = repository.resendEmail(request)){
                is ResultWrapper.Success -> {
                    _uiMessage.send(result.data)
                }
                is ResultWrapper.Error -> {
                    _uiMessage.send(result.message ?: "Bir hata oluştu")
                }
            }

            _verificationState.update {
                it.copy(
                    isLoading = false
                )
            }
        }
    }


    fun verifyEmail(token:String){
        if(_verificationState.value.isEmailVerified || _verificationState.value.isLoading)
            return
        screenModelScope.launch {
            _verificationState.update {
                it.copy(
                    isLoading = true,
                    verificationError = null
                )
            }

            when(val result = repository.verifyEmail(token)){
                is ResultWrapper.Success -> {
                    _uiMessage.send(result.data["message"] ?: "Başarılı")
                    _verificationState.update {
                        it.copy(
                            isLoading = false,
                            isEmailVerified = true
                        )
                    }

                    delay(2000)
                }

                is ResultWrapper.Error -> {
                    _verificationState.update {
                        it.copy(
                            isLoading = false,
                            verificationError = result.message ?: "Doğrulama başarısız."
                        )
                    }

                }
            }
        }
    }
    private suspend fun isEmailVerified(
        userId:Long
    ):Boolean{
        return when(val result = repository.isEmailUsedByDevice(userId)){
            is ResultWrapper.Success -> {
                if(result.data){
                    clearContext()
                    _uiMessage.send("Email doğrulandı")
                    _navigationState.send(EmailNavEffect.OnSuccess)
                    true
                }
                else{
                    false
                }
            }
            is ResultWrapper.Error -> {
                _uiMessage.send(result.message)
                false
            }
        }
    }


    private fun startWebSocket(deviceId: String) {
        if (!isConnecting.compareAndSet(false, true)) return
        webSocketJob?.cancel()

        webSocketJob = screenModelScope.launch {
            val eventJob = launch { collectEmailEvents() }

            try {
                while (isActive && !lifecycleStopped) {
                    try {
                        if (wasBackgrounded) {
                            val context = verificationContext
                            if (context != null && isEmailVerified(context.userId)) {
                                wasBackgrounded = false
                                break
                            }
                            wasBackgrounded = false
                        }
                        authWebSocketClient.connect(deviceId)
                    } catch (e: Exception) {
                        println("WS Reconnect Error: ${e.message}")
                    }

                    if (!isActive || lifecycleStopped) break
                    delay(5000) // Reconnect delay
                }
            } finally {
                eventJob.cancel()
                isConnecting.store(false)
            }
        }
    }
    fun handleLifecycleEvent(event: AuthLifecycleEvent) {
        println("FORGOT_LOG MODEL EVENT: $event")
        when (event) {
            AuthLifecycleEvent.Resume -> {
                lifecycleStopped = false

                if (lifecycleJob?.isActive == true || isConnecting.load() || webSocketJob?.isActive == true) {
                    return
                }

                lifecycleJob = screenModelScope.launch {
                    val context = verificationContext
                    if (context != null) {
                        println("FORGOT_LOG: API kontrolü başlatılıyor -> ${context.deviceId}")

                        try {
                            val changed = isEmailVerified(context.userId)
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
                lifecycleStopped = true
                wasBackgrounded = true // <-- SENIOR DOKUNUŞ: Arka plana geçtiğimizi kaydettik
                lifecycleJob?.cancel()
                stopWebSocket()
            }
            AuthLifecycleEvent.Pause -> Unit
        }
    }

    private suspend fun collectEmailEvents(){
        authWebSocketClient.events.collect { event ->
            when(event.type){
                SecurityEventType.EMAIL_VERIFIED -> {
                    println("EMailverfııedcalıstı")
                    stopWebSocket()
                    clearContext()
                    _navigationState.send(EmailNavEffect.OnSuccess)
                }
                else -> Unit
            }
        }
    }

    fun stopWebSocket(){
        isConnecting.store(false)
        webSocketJob?.cancel()
        screenModelScope.launch { authWebSocketClient.disconnect() }
    }

    private fun clearContext(){
        verificationContext = null
    }

}
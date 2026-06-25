package org.beem.tastymap.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.beem.tastymap.core.util.AppConfig
import org.beem.tastymap.data.model.domain.SecurityEvent
import org.beem.tastymap.data.model.domain.toDomain
import org.beem.tastymap.data.model.remote.SecurityEventDTO

class AuthWebSocketClient(private val client: HttpClient) {
    private val _events = MutableSharedFlow<SecurityEvent>()
    val events = _events.asSharedFlow()

    private var session: WebSocketSession? = null
    private var job: Job? = null

    suspend fun connect(deviceId: String) {
        println("WS: startWebSocket çağrıldı. Hedef: ${AppConfig.getWebSocketUrl(deviceId)}")

        if (job?.isActive == true) return

        job = CoroutineScope(Dispatchers.Default).launch {
            try {
                client.webSocket(AppConfig.getWebSocketUrl(deviceId)) {
                    println("WS: Bağlantı başarıyla kuruldu! (Session: $this)")
                    session = this

                    // Bu for döngüsü sunucudan mesaj bekler
                    for (frame in incoming) {
                        println("WS: Yeni bir frame geldi: ${frame.frameType}")
                        if (frame is Frame.Text) {
                            val json = frame.readText()
                            println("WS: Gelen veri -> $json")
                            val event = Json.decodeFromString<SecurityEventDTO>(json)
                            _events.emit(event.toDomain())
                        }
                    }
                    println("WS: For döngüsü bitti (sunucu bağlantıyı kapattı).")
                }
            } catch (e: Exception) {
                println("WS HATA DETAYI: ${e.stackTraceToString()}")
            } finally {
                println("WS: Bağlantı finally bloğunda kapandı.")
                session = null
                job = null
            }
        }
    }
     suspend fun disconnect() {
        job?.cancel()
        session?.close()
        job = null
        session = null
    }
}
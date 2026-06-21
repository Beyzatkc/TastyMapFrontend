package org.beem.tastymap.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.json.Json
import org.beem.tastymap.core.util.AppConfig
import org.beem.tastymap.data.model.domain.SecurityEvent
import org.beem.tastymap.data.model.domain.toDomain
import org.beem.tastymap.data.model.remote.SecurityEventDTO

class AuthWebSocketClient(private val client: HttpClient) {
    private val _events = MutableSharedFlow<SecurityEvent>()
    val events = _events.asSharedFlow()

    suspend fun connect(deviceId:String){
        val url = AppConfig.getWebSocketUrl(deviceId)
        client.webSocket(url){
            try{
                for(frame in incoming){
                    if (frame is Frame.Text) {
                        val json = frame.readText()
                        val event = Json.decodeFromString<SecurityEventDTO>(json)
                        _events.emit(event.toDomain())
                    }
                }
            }catch (e: Exception) {
                println("HATA: AUTHWEBSOCKETCLIENT - WebSocket bağlantısı koptu veya hata oluştu: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}
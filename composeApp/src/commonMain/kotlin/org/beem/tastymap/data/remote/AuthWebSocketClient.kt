package org.beem.tastymap.data.remote
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.beem.tastymap.core.util.AppConfig
import org.beem.tastymap.data.model.domain.SecurityEvent
import org.beem.tastymap.data.model.domain.toDomain
import org.beem.tastymap.data.model.remote.SecurityEventDTO
class AuthWebSocketClient(
    private val client: HttpClient
) { private val _events = MutableSharedFlow<SecurityEvent>(
        replay = 0
    )
    val events = _events.asSharedFlow()
    private var session: WebSocketSession? = null
    suspend fun connect(deviceId: String) {
        println(
            "WS CONNECT: ${AppConfig.getWebSocketUrl(deviceId)}"
        )
        try {
            client.webSocket(
                AppConfig.getWebSocketUrl(deviceId)
            ) {
                session = this
                println("WS CONNECTED")
                val pingJob = launch {
                    while (isActive) {
                        delay(30000)
                        try {
                            send(
                                Frame.Text("ping")
                            )
                        }
                        catch (e: Exception){
                            println(
                                "Ping failed ${e.message}"
                            )
                            break
                        }
                    }
                }
                try {
                    for(frame in incoming){
                        if(frame is Frame.Text){
                            val json = frame.readText()
                            println(
                                "WS EVENT: $json"
                            )
                            if(json=="pong")
                                continue
                            val dto = Json.decodeFromString<SecurityEventDTO>(json)
                            _events.emit(
                                dto.toDomain()
                            )
                        }
                    }
                }
                finally {

                    pingJob.cancel()
                    println("WS CLOSED")
                }
            }


        }
        catch(e:Exception){

            println(
                "WS ERROR: ${e.message}"
            )
        }
        finally {
            session=null
        }
    }
    suspend fun disconnect(){

        try{
            session?.close()
        }
        catch(_:Exception){}

        session=null
    }
}
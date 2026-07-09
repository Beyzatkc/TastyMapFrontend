package org.beem.tastymap.core.navigation

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow

// WebMessenger.kt
class WebMessenger : PlatformMessenger {
    private val channel = BroadcastChannel("tastymap_auth_channel")

    private val _messageChannel = Channel<String>(Channel.BUFFERED)

    init {
        channel.onmessage = { event ->
            println("Web: Klon sekmeden mesaj geldi: ${event.data}")
            // Gelen mesajı kuyruğa güvenle ekliyoruz
            _messageChannel.trySend(event.data)
        }
    }

    override fun post(message: String) {
        println("Web: Kanala mesaj gönderiliyor: $message")
        channel.postMessage(message)
    }

    override fun listen(): Flow<String> = _messageChannel.receiveAsFlow()

    override fun close() {
        _messageChannel.close()
        channel.close()
    }
}

@JsName("BroadcastChannel")
external class BroadcastChannel(name: String) {
    fun postMessage(message: String)
    var onmessage: (MessageEvent) -> Unit
    fun close()
}

external class MessageEvent {
    val data: String
}
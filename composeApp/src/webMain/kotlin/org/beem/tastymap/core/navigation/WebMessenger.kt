package org.beem.tastymap.core.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// WebMessenger.kt
class WebMessenger : PlatformMessenger {
    private val channel = BroadcastChannel("tastymap_auth_channel")

    private val _messageFlow = MutableStateFlow<String?>(null)

    init {
        channel.onmessage = { event ->
            _messageFlow.value = event.data
        }
    }
    override fun post(message: String) {
        channel.postMessage(message)
    }

    override fun listen(): StateFlow<String?> = _messageFlow


    override fun close() {
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
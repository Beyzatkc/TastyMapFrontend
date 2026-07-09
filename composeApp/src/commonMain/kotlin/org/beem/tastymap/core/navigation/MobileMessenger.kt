package org.beem.tastymap.core.navigation

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class MobileMessenger(): PlatformMessenger {
    private val _messageFlow = MutableSharedFlow<String>(replay = 0)

    override fun post(message: String) {
        _messageFlow.tryEmit(message)
    }

    override fun listen(): SharedFlow<String> = _messageFlow.asSharedFlow()

    override fun close() {

    }

}
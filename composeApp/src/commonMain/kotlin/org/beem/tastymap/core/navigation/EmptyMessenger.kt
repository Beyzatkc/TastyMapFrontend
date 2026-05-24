package org.beem.tastymap.core.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EmptyMessenger(): PlatformMessenger {
    private val _messageFlow = MutableStateFlow<String?>(null)
    override fun post(message: String) {
    }

    override fun listen(): StateFlow<String?> {
        return _messageFlow.asStateFlow()
    }


    override fun close() {
    }

}
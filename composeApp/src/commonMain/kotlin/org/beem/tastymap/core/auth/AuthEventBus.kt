package org.beem.tastymap.core.auth

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class AuthEventBus {
    private val _events = MutableSharedFlow<AuthEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    fun emitUnauthenticated() {
        _events.tryEmit(AuthEvent.OnUnauthenticated)
    }

    sealed interface AuthEvent {
        data object OnUnauthenticated : AuthEvent
    }
}
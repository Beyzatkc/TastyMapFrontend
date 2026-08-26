package org.beem.tastymap.core.auth

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class AuthEventBus {
    private val _events = MutableSharedFlow<AuthEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    fun emit(event: AuthEvent) {
        _events.tryEmit(event)
    }

    fun emitUnauthenticated() {
        _events.tryEmit(AuthEvent.OnSessionExpired)
    }

    sealed interface AuthEvent {
        data object OnSessionExpired : AuthEvent
        data object OnPasswordChanged : AuthEvent
        data object OnLoggedOut : AuthEvent
    }
}
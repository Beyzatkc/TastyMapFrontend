package org.beem.tastymap.core.auth

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow

class AuthEventBus {
    private val _events = Channel<AuthEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun emit(event: AuthEvent) {
        _events.trySend(event)
    }

    fun emitUnauthenticated() {
        _events.trySend(AuthEvent.OnSessionExpired)
    }


    sealed interface AuthEvent {
        data object OnSessionExpired : AuthEvent
        data object OnPasswordChanged : AuthEvent
        data object OnLoggedOut : AuthEvent
    }
}
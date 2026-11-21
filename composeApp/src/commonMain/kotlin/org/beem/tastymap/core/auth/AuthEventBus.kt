package org.beem.tastymap.core.auth

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class AuthEventBus {
    private val _events = MutableSharedFlow<AuthEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events = _events.asSharedFlow()

    fun emit(event: AuthEvent) {
        _events.tryEmit(event)
    }

    sealed interface AuthEvent {
        data object OnSessionExpired : AuthEvent
        data object OnPasswordChanged : AuthEvent
        data object OnLoggedOut : AuthEvent
        data object DeleteAccount : AuthEvent
    }
}
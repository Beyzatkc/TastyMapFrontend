package org.beem.tastymap.ui.auth.common

sealed interface AuthEffect {
    object NavigateToHome : AuthEffect

    object NavigateToLogin : AuthEffect

    data class NavigateToPending(val deviceId: String) : AuthEffect
    data class NavigateToValidate(val email: String, val deviceId: String): AuthEffect

}


package org.beem.tastymap.ui.auth

sealed interface AuthEffect {
    object NavigateToHome : AuthEffect

    object NavigateToLogin : AuthEffect

    object NavigateToPending : AuthEffect
    data class NavigateToValidate(val email: String): AuthEffect

}
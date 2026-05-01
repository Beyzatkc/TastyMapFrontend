package org.beem.tastymap.ui.auth

sealed interface AuthEffect {
    object NavigateToHome : AuthEffect

    object NavigateToLogin : AuthEffect

    object NavigateToPending : AuthEffect
    object NavigateToValidate : AuthEffect

    data class ShowMessage(val message: String) : AuthEffect
}
package org.beem.tastymap.ui.auth.common

sealed interface AuthEffect {
    object NavigateToHome : AuthEffect

    object NavigateToLogin : AuthEffect
    object NavigateToWelcome : AuthEffect

    data class NavigateToPending(val deviceId: String) : AuthEffect
    data class NavigateToValidate(val email: String, val deviceId: String, val userId: Long): AuthEffect

    data class NavigateToPasswordSuccess(
        val userId: Long,
        val deviceId: String
    ): AuthEffect


}


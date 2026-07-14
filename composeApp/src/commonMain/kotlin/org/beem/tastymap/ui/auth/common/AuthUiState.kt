package org.beem.tastymap.ui.auth.common
data class PasswordStrength(
    val hasMinLength: Boolean = false,
    val hasUppercase: Boolean = false,
    val hasDigit: Boolean = false,
    val hasSpecialChar: Boolean = false
)


sealed class AuthLifecycleEvent {
    data object Resume : AuthLifecycleEvent()
    data object Pause : AuthLifecycleEvent()
    data object Stop : AuthLifecycleEvent()
}





















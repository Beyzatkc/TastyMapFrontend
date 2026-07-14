package org.beem.tastymap.ui.auth.logReg

sealed class LoginEvent {
    data class UsernameChanged(val value: String) : LoginEvent()
    data class PasswordChanged(val value: String) : LoginEvent()
}
sealed class RegisterEvent {
    data class NameChanged(val value: String) : RegisterEvent()
    data class SurnameChanged(val value: String) : RegisterEvent()
    data class UsernameChanged(val value: String) : RegisterEvent()

    data class EmailChanged(val value: String) : RegisterEvent()
    data class PasswordChanged(val value: String) : RegisterEvent()
}
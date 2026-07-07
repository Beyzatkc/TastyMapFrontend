package org.beem.tastymap.ui.auth.common

data class LoginUiState(
    val loginUsername: String = "",
    val loginUsernameError: String? = null,
    val loginPassword: String = "",
    val logPasswordError: String? = null,
    val isLoading: Boolean = false
)

data class RegisterUiState(
    val regName: String = "",
    val regnameError: String? = null,
    val regSurname: String = "",
    val regSurnameError: String? = null,
    val regUsername: String = "",
    val regusernameError: String? = null,
    val step: Int = 1,

    val regEmail: String = "",
    val regEmailError: String? = null,
    val regPassword: String = "",
    val regPasswordError: String? = null,
    val isLoading: Boolean = false,
    val passwordStrength: PasswordStrength = PasswordStrength()
)
data class PasswordUiState(
    val regPassword: String = "",
    val regPasswordError: String? = null,
    val isLoading: Boolean = false,
    val passwordStrength: PasswordStrength = PasswordStrength()
)
data class PasswordEmailState(
    val pasEmail: String = "",
    val pasEmailError: String? = null,
    val isLoading: Boolean = false,
)

data class VerificationUiState(
    val verificationError: String? = null,
    val isEmailVerified: Boolean = false,
    val isLogin: Boolean = false,
    val isLoading: Boolean = false,
)
data class RequestState(
    val error: String? = null,
    val isLoading: Boolean = false,
)
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





















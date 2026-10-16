package org.beem.tastymap.ui.auth.forgotPassword

import org.beem.tastymap.ui.auth.common.PasswordStrength
import org.jetbrains.compose.resources.StringResource

data class PasswordUiState(
    val regPassword: String = "",
    val regPasswordError: StringResource? = null,
    val confirmPassword: String = "",
    val confirmPasswordError: StringResource? = null,
    val isLoading: Boolean = false,
    val isChanged: Boolean = false,
    val passwordStrength: PasswordStrength = PasswordStrength()
)
data class PasswordEmailState(
    val pasEmail: String = "",
    val pasEmailError: StringResource? = null,
    val isLoading: Boolean = false,
)
package org.beem.tastymap.ui.auth.forgotPassword

import org.beem.tastymap.ui.auth.common.PasswordStrength

data class PasswordUiState(
    val regPassword: String = "",
    val regPasswordError: String? = null,
    val confirmPassword: String = "",
    val confirmPasswordError: String? = null,
    val isLoading: Boolean = false,
    val isChanged: Boolean = false,
    val passwordStrength: PasswordStrength = PasswordStrength()
)
data class PasswordEmailState(
    val pasEmail: String = "",
    val pasEmailError: String? = null,
    val isLoading: Boolean = false,
)
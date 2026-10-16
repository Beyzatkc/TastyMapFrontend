package org.beem.tastymap.ui.auth.logReg

import org.beem.tastymap.ui.auth.common.PasswordStrength
import org.jetbrains.compose.resources.StringResource

data class LoginUiState(
    val loginUsername: String = "",
    val loginUsernameError: StringResource? = null,
    val loginPassword: String = "",
    val logPasswordError: StringResource? = null,
    val isLoading: Boolean = false,
    val isEmailNotVerified: Boolean = false,
    val unverifiedEmail: String = ""
)

    data class RegisterUiState(
        val regName: String = "",
        val regNameError: StringResource? = null,
        val regSurname: String = "",
        val regSurnameError: StringResource? = null,
        val regUsername: String = "",
        val regUsernameError: StringResource? = null,
        val step: Int = 1,

        val regEmail: String = "",
        val regEmailError: StringResource? = null,
        val regPassword: String = "",
        val regPasswordError: StringResource? = null,
        val isLoading: Boolean = false,
        val passwordStrength: PasswordStrength = PasswordStrength()
    )
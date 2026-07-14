package org.beem.tastymap.ui.auth.logReg

import org.beem.tastymap.ui.auth.common.PasswordStrength

    data class LoginUiState(
        val loginUsername: String = "",
        val loginUsernameError: String? = null,
        val loginPassword: String = "",
        val logPasswordError: String? = null,
        val isLoading: Boolean = false,
        val isEmailNotVerified: Boolean = false,
        val unverifiedEmail: String = "",
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
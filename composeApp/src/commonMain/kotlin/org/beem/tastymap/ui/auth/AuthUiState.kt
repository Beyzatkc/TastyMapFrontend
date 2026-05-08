package org.beem.tastymap.ui.auth

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
    val verificationError: String? = null
    )

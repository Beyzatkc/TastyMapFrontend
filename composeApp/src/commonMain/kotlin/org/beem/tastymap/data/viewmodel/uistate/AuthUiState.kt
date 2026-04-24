package org.beem.tastymap.data.viewmodel.uistate

import org.beem.tastymap.data.model.LoginResponse
import org.beem.tastymap.data.model.RegisterResponse

data class RegisterResponse(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val registerResponse: RegisterResponse? = null
)

data class LoginResponse(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val loginResponse: LoginResponse? = null
)
sealed interface AuthEffect {
    data object NavigateToHome : AuthEffect
    data object NavigateToPending : AuthEffect
    data object NavigateToLogin : AuthEffect
    data class ShowSnackbar(val message: String) : AuthEffect
}
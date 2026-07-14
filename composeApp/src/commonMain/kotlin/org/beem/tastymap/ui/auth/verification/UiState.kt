package org.beem.tastymap.ui.auth.verification


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
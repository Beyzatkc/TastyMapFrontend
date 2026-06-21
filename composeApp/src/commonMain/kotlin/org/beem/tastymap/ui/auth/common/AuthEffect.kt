package org.beem.tastymap.ui.auth.common

import org.beem.tastymap.data.model.ApprovedRefreshRequestDTO

sealed interface AuthEffect {
    object NavigateToHome : AuthEffect

    object NavigateToLogin : AuthEffect

    data class NavigateToPending(val approvedRefreshRequestDTO: ApprovedRefreshRequestDTO) : AuthEffect
    data class NavigateToValidate(val email: String): AuthEffect

}
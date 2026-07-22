package org.beem.tastymap.core.network

import org.beem.tastymap.core.local.TokenManager
import org.beem.tastymap.core.local.UserManager
import org.beem.tastymap.core.provider.AuthValidator
import org.beem.tastymap.data.model.auth.AuthStatus
class MobileSessionValidator(
    private val tokenManager: TokenManager,
    private val userManager: UserManager
) : AuthValidator {

    override suspend fun getAuthStatus(): AuthStatus {
        val token = tokenManager.getAccessToken()
        val userId = userManager.getUserId()
        val isOnboardingCompleted = userManager.getOnBoardComplete()

        if (token.isNullOrBlank() || userId == null || userId == -1L) {
            return AuthStatus.UNAUTHENTICATED
        }

        return if (isOnboardingCompleted == true) {
            AuthStatus.AUTHENTICATED
        } else {
            AuthStatus.NEEDS_ONBOARDING
        }
    }
}
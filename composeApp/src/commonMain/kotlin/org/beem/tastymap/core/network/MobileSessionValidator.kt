package org.beem.tastymap.core.network

import org.beem.tastymap.core.local.TokenManager
import org.beem.tastymap.core.local.UserManager
import org.beem.tastymap.core.provider.AuthValidator

class MobileSessionValidator(
    private val tokenManager: TokenManager,
    private val userManager: UserManager
): AuthValidator {
    override suspend fun isUserLoggedIn(): Boolean {
        val token = tokenManager.getAccessToken()
        val userId = userManager.getUserId()
        return !token.isNullOrBlank() && userId != -1L
    }
}
package org.beem.tastymap.core

import io.ktor.client.HttpClient
import org.beem.tastymap.core.provider.AuthValidator

class WebAuthValidator(
    private val noAuthClient: HttpClient
) : AuthValidator {
    override suspend fun isUserLoggedIn(): Boolean {
        return try {
            val response: UserStatusResponse = noAuthClient.get("api/users/me").body()
            response.isLoggedIn
        } catch (e: Exception) {
            false
        }
    }
}
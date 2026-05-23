package org.beem.tastymap.core

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.beem.tastymap.core.local.UserManager
import org.beem.tastymap.core.local.UserSession
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.network.safeApiCall
import org.beem.tastymap.core.provider.AuthValidator
import org.beem.tastymap.data.model.LoginStatus
import org.beem.tastymap.data.model.UserResponse


class WebSessionValidator(
    private val authClient: HttpClient,
    private val userManager: UserManager
) : AuthValidator {
    override suspend fun isUserLoggedIn(): Boolean {
        val userId = userManager.getUserId()
        if(userId != null){
            return true
        }
        val result = validateSession()
        return result is ResultWrapper.Success
    }
    suspend fun validateSession(): ResultWrapper<UserResponse> {
        return safeApiCall {
            val response = authClient.get("api/myProfile/me").body<UserResponse>()

            val session = UserSession(
                status = LoginStatus.SUCCESS.toString(),
                message = "Giriş yapılmış",
                userId = response.id,
                username = response.username,
                name = response.name,
                surname = response.surname,
                profile = response.profile,
                role = response.role,
                date = response.date,
                biography = response.biography
            )
            userManager.saveUser(session)

            response
        }
    }
}
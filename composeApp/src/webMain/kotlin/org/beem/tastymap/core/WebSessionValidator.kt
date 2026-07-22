package org.beem.tastymap.core
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.beem.tastymap.core.local.UserManager
import org.beem.tastymap.core.local.UserSession
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.network.safeApiCall
import org.beem.tastymap.core.provider.AuthValidator
import org.beem.tastymap.data.model.auth.AuthStatus
import org.beem.tastymap.data.model.auth.LoginStatus
import org.beem.tastymap.data.model.auth.UserResponse


class WebSessionValidator(
    private val authClient: HttpClient,
    private val userManager: UserManager
) : AuthValidator {

    override suspend fun getAuthStatus(): AuthStatus {
        val userId = userManager.getUserId()
        val isOnboardingCompleted = userManager.getOnBoardComplete()

        if (userId != null && isOnboardingCompleted == true) {
            return AuthStatus.AUTHENTICATED
        }else if(userId != null && isOnboardingCompleted == false){
            return AuthStatus.NEEDS_ONBOARDING
        }

        return when (val result = validateSession()) {
            is ResultWrapper.Success -> {
                val isCompleted = result.data.onboardingCompleted
                if (isCompleted) {
                    AuthStatus.AUTHENTICATED
                } else {
                    AuthStatus.NEEDS_ONBOARDING
                }
            }
            is ResultWrapper.Error -> AuthStatus.UNAUTHENTICATED
        }
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
                biography = response.biography,
                onBoardComplete = response.onboardingCompleted
            )
            println("Validate sessiona girdi")
            userManager.saveUser(session)

            response
        }
    }
}
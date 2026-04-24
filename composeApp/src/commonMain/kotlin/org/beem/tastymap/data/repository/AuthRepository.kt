package org.beem.tastymap.data.repository

import androidx.compose.animation.core.rememberTransition
import org.beem.tastymap.core.local.TokenManager
import org.beem.tastymap.data.model.LoginRequest
import org.beem.tastymap.data.model.LoginResponse
import org.beem.tastymap.data.model.LoginStatus
import org.beem.tastymap.data.model.RegisterRequest
import org.beem.tastymap.data.model.RegisterResponse
import org.beem.tastymap.data.remote.AuthDataSource

class AuthRepository(
    private val dataSource: AuthDataSource,
    private val tokenManager: TokenManager
) {
    suspend fun register(request: RegisterRequest): Result<RegisterResponse> {
        return runCatching {
            dataSource.register(request)
        }
    }

    suspend fun login(loginRequest: LoginRequest,userAgent:String): Result<LoginResponse>{
        return runCatching {
            val response= dataSource.login(loginRequest,userAgent)

            if (response.status == LoginStatus.SUCCESS && response.accessToken != null) {
                tokenManager.saveTokens(response.accessToken, response.refreshToken)
                tokenManager.saveDeviceId(loginRequest.deviceId)
            }

            response
        }
    }
}
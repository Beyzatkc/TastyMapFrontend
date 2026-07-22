package org.beem.tastymap.data.repository

import org.beem.tastymap.core.local.TokenManager
import org.beem.tastymap.core.local.UserManager
import org.beem.tastymap.core.local.UserSession
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.network.safeApiCall
import org.beem.tastymap.core.provider.AuthValidator
import org.beem.tastymap.data.model.auth.ApprovedRefreshRequestDTO
import org.beem.tastymap.data.model.auth.AuthStatus
import org.beem.tastymap.data.model.auth.CommonRequest
import org.beem.tastymap.data.model.auth.LoginRequest
import org.beem.tastymap.data.model.auth.LoginResponse
import org.beem.tastymap.data.model.auth.LoginStatus
import org.beem.tastymap.data.model.auth.NotificationResponse
import org.beem.tastymap.data.model.auth.RegisterRequest
import org.beem.tastymap.data.model.auth.ResetPassword
import org.beem.tastymap.data.model.auth.ResetPasswordResponse
import org.beem.tastymap.data.model.auth.UserResponse
import org.beem.tastymap.data.remote.AuthDataSource

class AuthRepository(
    private val dataSource: AuthDataSource,
    private val tokenManager: TokenManager,
    private val userManager: UserManager,
    private val authValidator: AuthValidator
) {
    suspend fun register(request: RegisterRequest): ResultWrapper<UserResponse> {
        return safeApiCall {
            dataSource.register(request)
        }
    }
    suspend fun login(loginRequest: LoginRequest,userAgent:String): ResultWrapper<LoginResponse>{
        return safeApiCall {
            val response= dataSource.login(loginRequest,userAgent)
            if (response.status == LoginStatus.SUCCESS && response.accessToken != null) {
                tokenManager.saveTokens(response.accessToken, response.refreshToken)
                tokenManager.saveDeviceId(loginRequest.deviceId)
                val user = UserSession(response.status.toString(),response.message,
                    response.userResponseDTO?.id,
                    response.userResponseDTO?.username,
                    response.userResponseDTO?.name,
                    response.userResponseDTO?.surname,
                    response.userResponseDTO?.profile,
                    response.userResponseDTO?.role, response.userResponseDTO?.date, response.userResponseDTO?.biography,response.userResponseDTO?.onboardingCompleted)

                userManager.saveUser(user)
            }
            response
        }
    }

    suspend fun verifyLogin(approvedRefreshRequestDTO: ApprovedRefreshRequestDTO): ResultWrapper<LoginResponse>{
        return safeApiCall {
            println("API çağrılıyor")
            val response = dataSource.verifyLogin(approvedRefreshRequestDTO)
            println("API döndü")

            if (response.status == LoginStatus.SUCCESS) {
                println("Token kaydediliyor buraya gıtrdı webde authrepo verıfylogın")
                tokenManager.saveTokens(response.accessToken, response.refreshToken)
                tokenManager.saveDeviceId(approvedRefreshRequestDTO.deviceId)
                val user = UserSession(response.status.toString(),response.message,
                    response.userResponseDTO?.id,
                    response.userResponseDTO?.username,
                    response.userResponseDTO?.name,
                    response.userResponseDTO?.surname,
                    response.userResponseDTO?.profile,
                    response.userResponseDTO?.role, response.userResponseDTO?.date, response.userResponseDTO?.biography,response.userResponseDTO?.onboardingCompleted)

                userManager.saveUser(user)
            }
            response
        }
    }
    suspend fun getAuthStatus(): AuthStatus {
        return authValidator.getAuthStatus()
    }


}
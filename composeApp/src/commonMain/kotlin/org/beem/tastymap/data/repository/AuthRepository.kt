package org.beem.tastymap.data.repository

import org.beem.tastymap.core.local.TokenManager
import org.beem.tastymap.core.local.UserManager
import org.beem.tastymap.core.local.UserSession
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.network.safeApiCall
import org.beem.tastymap.core.provider.AuthValidator
import org.beem.tastymap.data.model.ApprovedRefreshRequestDTO
import org.beem.tastymap.data.model.CommonRequest
import org.beem.tastymap.data.model.LoginRequest
import org.beem.tastymap.data.model.LoginResponse
import org.beem.tastymap.data.model.LoginStatus
import org.beem.tastymap.data.model.NotificationResponse
import org.beem.tastymap.data.model.RefreshTokenResponseDTO
import org.beem.tastymap.data.model.RegisterRequest
import org.beem.tastymap.data.model.UserResponse
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
                    response.userResponseDTO?.role, response.userResponseDTO?.date, response.userResponseDTO?.biography)

                userManager.saveUser(user)
            }
            response
        }
    }
    suspend fun resendEmail(commonRequest: CommonRequest): ResultWrapper<String>{
        return safeApiCall {
            val response = dataSource.resendMail(commonRequest)
            response
        }
    }
    suspend fun resendSecurityMail(deviceId: String): ResultWrapper<String>{
        return safeApiCall {
            val response = dataSource.resendSecurityMail(deviceId)
            response
        }
    }
    suspend fun verifyEmail(token: String): ResultWrapper<Map<String, String>>{
        return safeApiCall {
            val response = dataSource.verifyEmail(token)
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
                    response.userResponseDTO?.role, response.userResponseDTO?.date, response.userResponseDTO?.biography)

                userManager.saveUser(user)
            }
            response
        }
    }
    suspend fun isUserLoggedIn(): Boolean {
        return authValidator.isUserLoggedIn()
    }

    suspend fun isUsedNotification(deviceId: String): ResultWrapper<NotificationResponse>{
        return safeApiCall {
            dataSource.isUsedNotification(deviceId);
        }
    }

    suspend fun forgotPassword(commonRequest: CommonRequest): ResultWrapper<String>{
        return safeApiCall {
            dataSource.forgotPassword(commonRequest)
        }
    }

}
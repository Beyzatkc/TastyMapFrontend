package org.beem.tastymap.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import org.beem.tastymap.data.model.auth.ApprovedRefreshRequestDTO
import org.beem.tastymap.data.model.auth.CommonRequest
import org.beem.tastymap.data.model.auth.LoginRequest
import org.beem.tastymap.data.model.auth.LoginResponse
import org.beem.tastymap.data.model.auth.NotificationResponse
import org.beem.tastymap.data.model.auth.RegisterRequest
import org.beem.tastymap.data.model.auth.ResetPassword
import org.beem.tastymap.data.model.auth.ResetPasswordResponse
import org.beem.tastymap.data.model.auth.UserResponse

class UserSecurityDataSource(private val client: HttpClient) {

    suspend fun resendMail(request: CommonRequest): Long{
        return client.post("auth/resendMail") {
            setBody(request)
        }.body()
    }

    suspend fun verifyEmail(token: String): Map<String, String> {
        return client.get("auth/verify") {
            parameter("token", token)
        }.body()
    }
    suspend fun resendSecurityMail(deviceId: String): String{
        return client.post("auth/resend-security-mail") {
            parameter("deviceId",deviceId)

        }.body()
    }
    suspend fun isUsedNotification(deviceId: String): NotificationResponse{
        return client.get("auth/is-used") {
            parameter("deviceId",deviceId)
        }.body()
    }

    suspend fun forgotPassword(commonRequest: CommonRequest): ResetPasswordResponse{
        return client.post("auth/forgotPassword") {
            setBody(commonRequest)
        }.body()
    }

    suspend fun resetPassword(passwordRequest: ResetPassword): String{
        return client.post("auth/resetPassword"){
            setBody(passwordRequest)
        }.body()
    }
    suspend fun isEmailUsedByDevice(userId: Long): Boolean {
        return client.get("auth/check-used") {
            parameter("userId", userId)
        }.body()
    }
    suspend fun isPasswordUsedByDevice(userId: Long): Boolean {
        return client.get("auth/check-password") {
            parameter("userId", userId)
        }.body()
    }
}
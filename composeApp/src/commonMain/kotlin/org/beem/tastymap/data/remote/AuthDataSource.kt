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


class AuthDataSource(private val client: HttpClient) {
    suspend fun register(request: RegisterRequest): UserResponse {
        return client.post("api/users/register"){
            setBody(request)
        }.body()
    }
    suspend fun login(request: LoginRequest, userAgent: String): LoginResponse{
        return client.post("api/users/login") {
            setBody(request)
            header("User-Agent", userAgent)
        }.body()

    }
    suspend fun verifyLogin(
        request: ApprovedRefreshRequestDTO
    ): LoginResponse {
        return client.post("api/users/refresh/approved") {
            setBody(request)
        }.body()
    }

}















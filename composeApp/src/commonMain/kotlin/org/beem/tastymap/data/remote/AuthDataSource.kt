package org.beem.tastymap.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.beem.tastymap.data.model.ApprovedRefreshRequestDTO
import org.beem.tastymap.data.model.LoginRequest
import org.beem.tastymap.data.model.LoginResponse
import org.beem.tastymap.data.model.RefreshTokenResponseDTO
import org.beem.tastymap.data.model.RegisterRequest
import org.beem.tastymap.data.model.UserResponse


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

    suspend fun resendMail(email: String): String{
        return client.post("auth/resendMail") {
            parameter("email",email)
        }.body()
    }

    suspend fun verifyEmail(token: String): Map<String, String> {
        return client.get("auth/verify") {
            parameter("token", token)
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
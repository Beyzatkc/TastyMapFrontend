package org.beem.tastymap.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import org.beem.tastymap.data.model.LoginRequest
import org.beem.tastymap.data.model.LoginResponse
import org.beem.tastymap.data.model.RegisterRequest
import org.beem.tastymap.data.model.RegisterResponse

class AuthDataSource(private val client: HttpClient) {

    suspend fun register(request: RegisterRequest): RegisterResponse{
        return client.post("/api/users/register"){
            setBody(request)
        }.body()
    }
    suspend fun login(request: LoginRequest, userAgent: String): LoginResponse{
        return client.post("/api/users/login") {
            setBody(request)
            header("User-Agent", userAgent)
        }.body()
    }
}
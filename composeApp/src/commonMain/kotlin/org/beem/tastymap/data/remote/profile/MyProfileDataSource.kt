package org.beem.tastymap.data.remote.profile

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import org.beem.tastymap.data.model.auth.UserResponse
import org.beem.tastymap.data.model.profile.ActiveDevicesResponse
import org.beem.tastymap.data.model.profile.ChangePassword
import org.beem.tastymap.data.model.profile.MessageResponse
import org.beem.tastymap.data.model.profile.ProfileResponse
import org.beem.tastymap.data.model.profile.UpdateProfile

class MyProfileDataSource(private val client: HttpClient) {
    suspend fun getUserProfile(): ProfileResponse {
        return client.get("api/myProfile/meProfile").body()
    }
    suspend fun getActiveDevices(): ActiveDevicesResponse =
        client.get("api/myProfile/active").body()

    suspend fun logout(deviceId: String) {
        client.post("api/myProfile/logout") {
            parameter("deviceId", deviceId)
        }
    }
    suspend fun updateProfile(request: UpdateProfile): MessageResponse =
         client.post("api/myProfile/update") {
            setBody(request)
        }.body()

    suspend fun changePassword(request: ChangePassword): MessageResponse =
         client.post("api/myProfile/changePassword") {
            setBody(request)
        }.body()

    suspend fun getMe(): UserResponse =
        client.get("api/myProfile/me").body()

}
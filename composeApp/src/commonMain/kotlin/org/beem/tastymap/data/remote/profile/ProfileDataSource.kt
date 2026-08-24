package org.beem.tastymap.data.remote.profile

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.beem.tastymap.data.model.profile.ProfileResponse

class ProfileDataSource(private val client: HttpClient) {

     suspend fun getUserProfile(userId: Long): ProfileResponse {
        return client.get("api/userProfile/profile/$userId").body()
    }
}
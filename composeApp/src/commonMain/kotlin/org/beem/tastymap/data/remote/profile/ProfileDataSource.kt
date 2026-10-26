package org.beem.tastymap.data.remote.profile

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.beem.tastymap.core.network.AuthHttpClientManager
import org.beem.tastymap.data.model.profile.ProfileResponse

class ProfileDataSource(private val authHttpClientManager: AuthHttpClientManager) {
    private val client: HttpClient
        get() = authHttpClientManager.getClient()
     suspend fun getUserProfileId(userId: Long): ProfileResponse {
        return client.get("api/user-profile/id/$userId").body()
    }
    suspend fun getUserProfileUsername(username: String): ProfileResponse {
        return client.get("api/user-profile/username/$username").body()
    }
}
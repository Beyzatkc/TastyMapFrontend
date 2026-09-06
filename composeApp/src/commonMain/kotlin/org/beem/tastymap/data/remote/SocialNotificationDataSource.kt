package org.beem.tastymap.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.put
import org.beem.tastymap.data.model.PageResponse
import org.beem.tastymap.data.model.socialnotifications.SocialNotificationsResponse

class SocialNotificationDataSource(private val client: HttpClient) {

    suspend fun getNotifications(
        page: Int = 0,
        size: Int = 10
    ): PageResponse<SocialNotificationsResponse> {
        return client.get("api/socialnotifications") {
            parameter("page", page)
            parameter("size", size)
        }.body()
    }

    suspend fun markAsRead() {
        client.put("api/socialnotifications/read")
    }
}
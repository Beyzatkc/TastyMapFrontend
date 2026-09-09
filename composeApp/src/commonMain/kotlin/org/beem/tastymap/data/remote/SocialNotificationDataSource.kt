package org.beem.tastymap.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.put
import org.beem.tastymap.data.model.PageResponse
import org.beem.tastymap.data.model.socialnotifications.SocialNotificationsResponse
import org.beem.tastymap.data.model.subscribers.HasUnreadResponse

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

    suspend fun checkHasUnread(): Boolean {
        val response: HasUnreadResponse = client.get("api/socialnotifications/has-unread").body()
        return response.hasUnread
    }
}
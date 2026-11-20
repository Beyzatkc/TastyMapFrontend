package org.beem.tastymap.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import org.beem.tastymap.data.model.device.FcmTokenUpdateRequest
import org.beem.tastymap.data.model.device.NotificationPreferenceRequest

class UserDeviceDataSource(private val client: HttpClient) {
    suspend fun updateFcm(request: FcmTokenUpdateRequest) {
         client.post("api/v1/devices/fcm-token"){
             setBody(request)
         }
    }
    suspend fun updateNotificationPreference(request: NotificationPreferenceRequest) {
        client.put("api/v1/devices/notifications/preferences"){
            setBody(request)
        }
    }
    suspend fun getNotificationPreference(deviceId: String): Boolean {
        return client.get("api/v1/devices/notifications/preferences") {
            parameter("deviceId", deviceId)
        }.body()
    }
}
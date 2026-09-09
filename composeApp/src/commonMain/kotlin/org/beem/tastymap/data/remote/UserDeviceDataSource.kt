package org.beem.tastymap.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import org.beem.tastymap.data.model.FcmTokenUpdateRequest

class UserDeviceDataSource(private val client: HttpClient) {
    suspend fun updateFcm(request: FcmTokenUpdateRequest) {
         client.post("api/v1/devices/fcm-token"){
             setBody(request)
         }

    }
}
package org.beem.tastymap.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import org.beem.tastymap.data.model.PageResponse
import org.beem.tastymap.data.model.subscribers.SubscribeResponse

class SubscribersDataSource(private val client: HttpClient) {


    suspend fun subscribe(userId: Long): SubscribeResponse {
        return client.post("api/subscribe/$userId")
            .body()
    }

    suspend fun acceptSubscribeRequest(requesterId: Long) {
        client.post("api/subscribe/accept/$requesterId")
    }

    suspend fun rejectSubscribeRequest(requesterId: Long) {
        client.post("api/subscribe/reject/$requesterId")
    }

    suspend fun unSubscribe(userId: Long) {
        client.delete("api/subscribe/unSubscribe/$userId")
    }

    suspend fun unSubscriber(userId: Long) {
        client.delete("api/subscribe/unSubscriber/$userId")
    }
    //Abone Oldukları Listesi
    suspend fun getUserSubscribes(userId: Long, page: Int = 0, size: Int = 10): PageResponse<SubscribeResponse> {
        return client.get("api/subscribe/getSubscribe/$userId") {
            parameter("page", page)
            parameter("size", size)
        }.body()
    }

    //Abone Olanlar Listesi
    suspend fun getUserSubscribers(userId: Long, page: Int = 0, size: Int = 10): PageResponse<SubscribeResponse> {
        return client.get("api/subscribe/getSubscribers/$userId") {
            parameter("page", page)
            parameter("size", size)
        }.body()
    }

    // Onay Bekleyen Abonelik İstekleri Listesi
    suspend fun getPendingRequests(page: Int = 0, size: Int = 10): PageResponse<SubscribeResponse> {
        return client.get("api/subscribe/requests") {
            parameter("page", page)
            parameter("size", size)
        }.body()
    }


}
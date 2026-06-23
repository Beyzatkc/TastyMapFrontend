package org.beem.tastymap.place.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.beem.tastymap.data.model.BaseResponse
import org.beem.tastymap.place.model.review.ReviewResponse

class PlaceDataSource(private val client: HttpClient) {
    suspend fun getPlaceReviews(
        placeId: String,
        page: Int = 0,
        size: Int = 5
    ): BaseResponse<ReviewResponse> {
        return client.get("place-review/place-reviews/$placeId") {
            parameter("page", page)
            parameter("size", size)
        }.body<BaseResponse<ReviewResponse>>()
    }
}
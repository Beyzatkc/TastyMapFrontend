package org.beem.tastymap.place.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.contentType
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import org.beem.tastymap.core.network.DevTokenProvider
import org.beem.tastymap.data.model.BaseResponse
import org.beem.tastymap.place.model.details.PlaceDetailsResponse
import org.beem.tastymap.place.model.review.ReviewItem
import org.beem.tastymap.place.model.review.ReviewResponse
import org.beem.tastymap.review.model.CreatedReviewRes
import org.beem.tastymap.review.model.SentReviewReq
import org.beem.tastymap.review.model.UpdateReviewReq
import org.beem.tastymap.review.model.UpdatedReviewRes

class PlaceDataSource(private val client: HttpClient) {

    suspend fun getPlaceDetails(placeId: String): PlaceDetailsResponse {
        return client.get("places/nearby-details") {
            parameter("place_id", placeId)
            DevTokenProvider.authHeader?.let { header(HttpHeaders.Authorization, it) }
        }.body<PlaceDetailsResponse>()
    }

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


    suspend fun sendPlaceReview(
        request: SentReviewReq
    ): BaseResponse<ReviewItem> {
        return client.post("place-review/send-review") {
            contentType(ContentType.Application.Json)
            DevTokenProvider.authHeader?.let { header(HttpHeaders.Authorization, it) }
            setBody(request)
        }.body<BaseResponse<ReviewItem>>()
    }

    suspend fun updatePlaceReview(
        request: UpdateReviewReq
    ): BaseResponse<ReviewItem> {
        return client.patch("place-review/update-review") {
            contentType(ContentType.Application.Json)
            DevTokenProvider.authHeader?.let { header(HttpHeaders.Authorization, it) }
            setBody(request)
        }.body<BaseResponse<ReviewItem>>()
    }

    suspend fun deletePlaceReview(
        reviewId: Long
    ): BaseResponse<Boolean> {
        return client.delete("place-review/delete-review/$reviewId") {
            DevTokenProvider.authHeader?.let { header(HttpHeaders.Authorization, it) }
        }.body<BaseResponse<Boolean>>()
    }

}
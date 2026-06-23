package org.beem.tastymap.place.repository

import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.network.safeApiCall
import org.beem.tastymap.data.model.BaseResponse
import org.beem.tastymap.place.api.PlaceDataSource
import org.beem.tastymap.place.model.review.ReviewResponse

class PlaceRepository(private val placeDataSource: PlaceDataSource) {

    suspend fun loadReviews(placeId: String, page: Int, size: Int): ResultWrapper<BaseResponse<ReviewResponse>> {
        return safeApiCall {
            val response = placeDataSource.getPlaceReviews(placeId, page, size)
            response
        }
    }

}
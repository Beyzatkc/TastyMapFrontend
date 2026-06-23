package org.beem.tastymap.place

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.data.model.BaseResponse
import org.beem.tastymap.place.model.review.ReviewItem
import org.beem.tastymap.place.model.review.ReviewResponse
import org.beem.tastymap.place.repository.PlaceRepository
import kotlin.collections.emptyList

class RestaurantDetailScreenModel(
    private val repository: PlaceRepository
) : ScreenModel {

    suspend fun loadReviews(placeId: String, page: Int, size: Int):List<ReviewItem>{
        val response = repository.loadReviews(placeId, page, size)
        return when(response){
            is ResultWrapper.Success<BaseResponse<ReviewResponse>> -> {
                val data = response.data.data?.reviewList
                println(data)
                data ?: emptyList()
            }
            is ResultWrapper.Error -> {
                println("Hata Oluştu: ${response.message}")
                emptyList()
            }
        }
    }
}
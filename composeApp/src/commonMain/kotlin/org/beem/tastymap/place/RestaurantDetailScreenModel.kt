package org.beem.tastymap.place

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.paging.TastyPagingController
import org.beem.tastymap.core.paging.TastyPagingState
import org.beem.tastymap.data.model.BaseResponse
import org.beem.tastymap.place.model.review.ReviewItem
import org.beem.tastymap.place.model.review.ReviewResponse
import org.beem.tastymap.place.repository.PlaceRepository
import org.beem.tastymap.place.state.ReviewPagingState
import kotlin.collections.emptyList

class RestaurantDetailScreenModel(
    private val repository: PlaceRepository
) : ScreenModel {
    private val _reviewsPagingState = MutableStateFlow(TastyPagingState<ReviewItem>())
    val reviewsPagingState: StateFlow<TastyPagingState<ReviewItem>> = _reviewsPagingState.asStateFlow()

    private var currentPlaceId: String? = null
    private var pagingController: TastyPagingController<ReviewItem, Long>? = null

    private var collectJob: Job? = null

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

    fun loadReviewsForPlace(placeId: String) {
        if (currentPlaceId == placeId && pagingController != null) return

        currentPlaceId = placeId

        collectJob?.cancel()
        pagingController?.reset()

        _reviewsPagingState.value = TastyPagingState()

        println("placeId: $placeId")

        pagingController = TastyPagingController(
            pageSize = 5,
            scope = screenModelScope,
            itemKeySelector = { it.id },
            fetchPage = { page, pageSize ->
                when (val response = repository.loadReviews(placeId, page, pageSize)) {
                    is ResultWrapper.Success -> {
                        response.data.data?.reviewList.orEmpty()
                    }
                    is ResultWrapper.Error -> {
                        println("ScreenModel Paging Hatalı Yanıt: ${response.message}")
                        throw Exception(response.message ?: "Yorumlar yüklenemedi")
                    }
                }
            }

        )

        collectJob = screenModelScope.launch {
            pagingController?.state?.collect { newState ->
                _reviewsPagingState.value = newState
            }
        }
        pagingController?.loadNextPage()
    }

    fun loadMoreReviews() {
        println("Atlas: loadMoreReviews çağrıldı. Controller null mı? -> ${pagingController == null}")
        println("Atlas: Mevcut State -> isLoading: ${pagingController?.state?.value?.isLoading}, isEndReached: ${pagingController?.state?.value?.isEndReached}")
        pagingController?.loadNextPage()
    }

    fun resetState() {
        currentPlaceId = null
        collectJob?.cancel()
        pagingController?.reset()
        _reviewsPagingState.value = TastyPagingState()
    }
}
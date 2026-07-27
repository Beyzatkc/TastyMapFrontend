package org.beem.tastymap.place

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
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

    private val _uiState = MutableStateFlow(ReviewPagingState())
    val uiState = _uiState.asStateFlow()

    private var pagingController: TastyPagingController<ReviewItem>? = null

    private var currentPlaceId: String? = null
    val reviewsState: StateFlow<TastyPagingState<ReviewItem>>?
        get() = pagingController?.state

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

    fun loadReviews(placeId: String, isRefresh: Boolean = false) {
        val currentState = _uiState.value

        if (currentState.isLoading) return
        if (!isRefresh && currentState.isEndReached && currentState.currentPlaceId == placeId) return

        val targetPage = if (isRefresh || currentState.currentPlaceId != placeId) 0 else currentState.page

        if (currentState.currentPlaceId != placeId || isRefresh) {
            _uiState.value = ReviewPagingState(isLoading = true, currentPlaceId = placeId)
        } else {
            _uiState.update { it.copy(isLoading = true) }
        }

        screenModelScope.launch {
            val response = repository.loadReviews(placeId, targetPage, size = 5)
            when (response) {
                is ResultWrapper.Success -> {
                    val newItems = response.data.data?.reviewList.orEmpty()
                    _uiState.update { state ->
                        val updatedList = if (targetPage == 0) newItems else state.items + newItems
                        state.copy(
                            items = updatedList,
                            page = targetPage + 1,
                            isLoading = false,
                            isEndReached = newItems.isEmpty() || newItems.size < 5,
                            currentPlaceId = placeId
                        )
                    }
                }
                is ResultWrapper.Error -> {
                    println("Hata Oluştu: ${response.message}")
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun loadReviewsForPlace(placeId: String) {
        if (currentPlaceId == placeId && pagingController != null) return

        currentPlaceId = placeId
        pagingController?.reset()

        pagingController = TastyPagingController(
            pageSize = 5,
            scope = screenModelScope,
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
        ).also {
            it.loadNextPage()
        }
    }

    fun loadMoreReviews() {
        pagingController?.loadNextPage()
    }

    fun resetState() {
        currentPlaceId = null
        pagingController?.reset()
        pagingController = null
    }
}
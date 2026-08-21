package org.beem.tastymap.place

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.paging.TastyPagingController
import org.beem.tastymap.core.paging.TastyPagingState
import org.beem.tastymap.data.model.BaseResponse
import org.beem.tastymap.place.model.details.PlaceDetailsResult
import org.beem.tastymap.place.model.review.ReviewItem
import org.beem.tastymap.place.model.review.ReviewResponse
import org.beem.tastymap.place.repository.PlaceRepository

class RestaurantDetailScreenModel(
    private val repository: PlaceRepository
) : ScreenModel {

    // --- Paging Yorumlar State ---
    private val _reviewsPagingState = MutableStateFlow(TastyPagingState<ReviewItem>())
    val reviewsPagingState: StateFlow<TastyPagingState<ReviewItem>> = _reviewsPagingState.asStateFlow()

    // --- Mekan Detayı & Kullanıcının Kendi Yorumu State ---
    private val _placeDetails = MutableStateFlow<PlaceDetailsResult?>(null)
    val placeDetails: StateFlow<PlaceDetailsResult?> = _placeDetails.asStateFlow()

    private val _isDetailsLoading = MutableStateFlow(false)
    val isDetailsLoading: StateFlow<Boolean> = _isDetailsLoading.asStateFlow()

    private var currentPlaceId: String? = null
    private var pagingController: TastyPagingController<ReviewItem, Long>? = null
    private var collectJob: Job? = null

    // Mekan detayını ve kullanıcının yorumunu çeker
    fun loadPlaceDetails(placeId: String) {
        screenModelScope.launch {
            _isDetailsLoading.value = true
            val details = repository.fetchPlaceDetails(placeId)
            _placeDetails.value = details
            _isDetailsLoading.value = false
        }
    }

    suspend fun loadReviews(placeId: String, page: Int, size: Int): List<ReviewItem> {
        val response = repository.loadReviews(placeId, page, size)
        return when (response) {
            is ResultWrapper.Success<BaseResponse<ReviewResponse>> -> {
                val data = response.data.data?.reviewList
                data ?: emptyList()
            }
            is ResultWrapper.Error -> {
                println("Hata Oluştu: ${response.message}")
                emptyList()
            }
        }
    }

    // Mekan açıldığında hem detayları hem yorumları başlatan ana fonksiyon
    fun loadPlaceData(placeId: String) {
        loadPlaceDetails(placeId)
        loadReviewsForPlace(placeId)
    }

    fun loadReviewsForPlace(placeId: String) {
        if (currentPlaceId == placeId && pagingController != null) return

        currentPlaceId = placeId

        collectJob?.cancel()
        pagingController?.reset()

        _reviewsPagingState.value = TastyPagingState()

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
        pagingController?.loadNextPage()
    }


    fun resetState() {
        currentPlaceId = null
        collectJob?.cancel()
        pagingController?.reset()
        _reviewsPagingState.value = TastyPagingState()
        _placeDetails.value = null
        _isDetailsLoading.value = false
    }
}
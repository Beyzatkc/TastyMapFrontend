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
import org.beem.tastymap.place.model.details.PlaceDetailsResult
import org.beem.tastymap.place.model.review.ReviewItem
import org.beem.tastymap.place.model.review.ReviewResponse
import org.beem.tastymap.place.repository.PlaceRepository
import org.beem.tastymap.place.state.PlaceDetailsUiState
import org.beem.tastymap.place.state.RestaurantDetailIntent

class RestaurantDetailScreenModel(
    private val repository: PlaceRepository
) : ScreenModel {

    // --- Paging Yorumlar State ---
    private val _reviewsPagingState = MutableStateFlow(TastyPagingState<ReviewItem>())
    val reviewsPagingState: StateFlow<TastyPagingState<ReviewItem>> = _reviewsPagingState.asStateFlow()

    // --- Mekan Detayı & Kullanıcının Kendi Yorumu State ---
    private val _detailsUiState = MutableStateFlow(PlaceDetailsUiState())
    val detailsUiState: StateFlow<PlaceDetailsUiState> = _detailsUiState.asStateFlow()

    private var currentPlaceId: String? = null
    private var pagingController: TastyPagingController<ReviewItem, Long>? = null
    private var collectJob: Job? = null


    fun handleIntent(intent: RestaurantDetailIntent) {
        when (intent) {
            is RestaurantDetailIntent.QuickScoreChanged -> {
                _detailsUiState.update { it.copy(quickScore = intent.score) }
            }
            is RestaurantDetailIntent.OpenAddReview -> {
                _detailsUiState.update {
                    it.copy(quickScore = intent.initialScore, isAddReviewOpen = true)
                }
            }
            is RestaurantDetailIntent.DismissAddReview -> {
                _detailsUiState.update {
                    it.copy(quickScore = 0.0, isAddReviewOpen = false)
                }
            }
            is RestaurantDetailIntent.ReviewSubmittedSuccess -> {
                _detailsUiState.update {
                    it.copy(quickScore = 0.0, isAddReviewOpen = false)
                }
                currentPlaceId?.let { loadPlaceData(it) }
            }
            is RestaurantDetailIntent.RetryDetails -> {
                currentPlaceId?.let { loadPlaceDetails(it, forceRefresh = true) }
            }
            is RestaurantDetailIntent.LoadMoreReviews -> {
                loadMoreReviews()
            }
            is RestaurantDetailIntent.DismissMainSheet -> {
                resetState()
            }
        }
    }

    // Mekan detayını ve kullanıcının yorumunu çeker
    fun loadPlaceDetails(placeId: String, forceRefresh: Boolean = false) {
        screenModelScope.launch {
            _detailsUiState.update { state ->
                state.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }
            val details = repository.fetchPlaceDetails(placeId, forceRefresh)
            when (details) {
                is ResultWrapper.Error -> {
                    _detailsUiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = details.message
                        )
                    }
                }
                is ResultWrapper.Success -> {
                    _detailsUiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = null,
                            details = details.data
                        )
                    }
                }
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
        _detailsUiState.value = PlaceDetailsUiState()
    }
}
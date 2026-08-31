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
import org.beem.tastymap.place.model.review.ReviewItem
import org.beem.tastymap.place.repository.PlaceRepository
import org.beem.tastymap.place.state.PlaceDetailsUiState
import org.beem.tastymap.place.state.RestaurantDetailIntent
import org.beem.tastymap.place.util.RatingCalculator

class RestaurantDetailScreenModel(
    private val repository: PlaceRepository
) : ScreenModel {

    private val _reviewsPagingState = MutableStateFlow(TastyPagingState<ReviewItem>())
    val reviewsPagingState: StateFlow<TastyPagingState<ReviewItem>> = _reviewsPagingState.asStateFlow()

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
            is RestaurantDetailIntent.RetryDetails -> {
                currentPlaceId?.let { loadPlaceDetails(it, forceRefresh = true) }
            }
            is RestaurantDetailIntent.LoadMoreReviews -> {
                loadMoreReviews()
            }
            is RestaurantDetailIntent.DismissMainSheet -> {
                resetState()
            }

            // 1. OLUŞTURULDU
            is RestaurantDetailIntent.ReviewCreatedLocally -> {
                _detailsUiState.update { state ->
                    val (newRating, newCount) = RatingCalculator.onReviewAdded(
                        currentRating = state.details?.tastyMapRating ?: 0.0,
                        currentCount = state.details?.tastyMapReviewCount ?: 0,
                        newScore = intent.review.rating
                    )

                    val updatedStats = RatingCalculator.onStatsAdded(
                        currentStats = state.details?.stats,
                        newRating = intent.review.rating,
                        newScores = intent.review.scores
                    )

                    state.copy(
                        isAddReviewOpen = false,
                        quickScore = intent.review.rating,
                        details = state.details?.copy(
                            userReview = intent.userSummary,
                            tastyMapRating = newRating,
                            tastyMapReviewCount = newCount,
                            stats = updatedStats
                        )
                    )
                }
                _reviewsPagingState.update { paging ->
                    paging.copy(items = listOf(intent.review) + paging.items)
                }
            }

            // 2. GÜNCELLENDİ
            is RestaurantDetailIntent.ReviewUpdatedLocally -> {
                _detailsUiState.update { state ->
                    val oldScore = state.details?.userReview?.rating ?: intent.review.rating
                    val oldScores = state.details?.userReview?.scores ?: emptyList()

                    val updatedRating = RatingCalculator.onReviewUpdated(
                        currentRating = state.details?.tastyMapRating ?: 0.0,
                        currentCount = state.details?.tastyMapReviewCount ?: 1,
                        oldScore = oldScore,
                        newScore = intent.review.rating
                    )

                    val updatedStats = RatingCalculator.onStatsUpdated(
                        currentStats = state.details?.stats,
                        oldRating = oldScore,
                        newRating = intent.review.rating,
                        oldScores = oldScores,
                        newScores = intent.review.scores
                    )

                    state.copy(
                        isAddReviewOpen = false,
                        quickScore = intent.review.rating,
                        details = state.details?.copy(
                            userReview = intent.userSummary,
                            tastyMapRating = updatedRating,
                            stats = updatedStats
                        )
                    )
                }
                _reviewsPagingState.update { paging ->
                    val updatedList = paging.items.map { item ->
                        if (item.id == intent.review.id) intent.review else item
                    }
                    paging.copy(items = updatedList)
                }
            }

            // 3. SİLİNDİ
            is RestaurantDetailIntent.ReviewDeletedLocally -> {
                _detailsUiState.update { state ->
                    val userReviewScores = state.details?.userReview?.scores ?: emptyList()

                    val (newRating, newCount) = RatingCalculator.onReviewDeleted(
                        currentRating = state.details?.tastyMapRating ?: 0.0,
                        currentCount = state.details?.tastyMapReviewCount ?: 0,
                        deletedScore = intent.deletedScore
                    )

                    val updatedStats = RatingCalculator.onStatsDeleted(
                        currentStats = state.details?.stats,
                        deletedRating = intent.deletedScore,
                        deletedScores = userReviewScores
                    )

                    state.copy(
                        isAddReviewOpen = false,
                        quickScore = 0.0,
                        details = state.details?.copy(
                            userReview = null,
                            tastyMapRating = newRating,
                            tastyMapReviewCount = newCount,
                            stats = updatedStats
                        )
                    )
                }
                _reviewsPagingState.update { paging ->
                    paging.copy(items = paging.items.filterNot { it.id == intent.reviewId })
                }
            }

            else -> {}
        }
    }

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

    fun loadPlaceData(placeId: String, forceRefresh: Boolean = false) {
        loadPlaceDetails(placeId, forceRefresh)
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
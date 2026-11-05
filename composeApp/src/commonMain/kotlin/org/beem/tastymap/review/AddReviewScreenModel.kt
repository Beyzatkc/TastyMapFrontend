package org.beem.tastymap.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.place.model.review.UserReviewSummaryDto
import org.beem.tastymap.place.repository.PlaceRepository
import org.beem.tastymap.review.model.ScoreType

class AddReviewScreenModel(
    private val placeRepository: PlaceRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddReviewState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<AddReviewEvent>(extraBufferCapacity = 1)
    val event = _event.asSharedFlow()

    fun setupReviewForm(existingReview: UserReviewSummaryDto?, defaultScore: Double = 0.0) {
        if (existingReview != null) {
            val scoreMap = existingReview.scores.associate { it.type to it.score }
            val hasAdvancedCriteria = scoreMap.keys.any {
                it !in listOf(ScoreType.TASTE, ScoreType.SERVICE, ScoreType.PRICE_PERFORMANCE)
            }

            _state.value = AddReviewState(
                isEditMode = true,
                reviewId = existingReview.reviewId,
                mainScore = existingReview.rating,
                subScores = scoreMap,
                comment = existingReview.text.orEmpty(),
                isAdvancedExpanded = hasAdvancedCriteria
            )
        } else {
            _state.value = AddReviewState(
                isEditMode = false,
                reviewId = null,
                mainScore = if (defaultScore > 0.0) defaultScore else 0.0,
                subScores = emptyMap(),
                comment = ""
            )
        }
    }

    fun initInitialScore(score: Double) {
        if (score > 0.0) {
            _state.value = _state.value.copy(mainScore = score)
        }
    }

    fun onMainScoreChange(score: Double) {
        _state.value = _state.value.copy(mainScore = score)
    }

    fun onCommentChange(comment: String) {
        _state.value = _state.value.copy(comment = comment)
    }

    fun onSubScoreChange(type: ScoreType, score: Double) {
        val currentScores = _state.value.subScores.toMutableMap()
        currentScores[type] = score
        _state.value = _state.value.copy(subScores = currentScores)
    }

    fun toggleAdvancedCriteria() {
        _state.value = _state.value.copy(
            isAdvancedExpanded = !_state.value.isAdvancedExpanded
        )
    }

    // 1. Yeni Yorum Gönderme (POST)
    fun submitReview(placeId: String) {
        val state = _state.value
        if (state.isSubmitting) return

        viewModelScope.launch {
            _state.value = state.copy(isSubmitting = true, errorMessage = null)
            val evaluatedScores = state.subScores.filter { it.value > 0.0 }

            when (val result = placeRepository.submitReview(
                placeId = placeId,
                mainScore = state.mainScore,
                comment = state.comment.trim(),
                scores = evaluatedScores
            )) {
                is ResultWrapper.Success -> {
                    _state.value = _state.value.copy(isSubmitting = false)
                    val reviewItem = result.data.data
                    if (reviewItem != null) {
                        val userSummary = UserReviewSummaryDto(
                            reviewId = reviewItem.id,
                            userName = reviewItem.name,
                            rating = reviewItem.rating,
                            text = reviewItem.content,
                            createdAt = reviewItem.createdAt,
                            updatedAt = reviewItem.updateAt,
                            scores = reviewItem.scores
                        )
                        _event.emit(AddReviewEvent.Created(review = reviewItem, userSummary = userSummary))
                    }
                }
                is ResultWrapper.Error -> {
                    val errorMsg = result.message ?: "Yorum gönderilirken bir hata oluştu"
                    _state.value = _state.value.copy(
                        isSubmitting = false,
                        errorMessage = errorMsg
                    )
                    _event.emit(AddReviewEvent.Error(errorMsg))
                }
            }
        }
    }

    // 2. Yorum Güncelleme (PATCH)
    fun updateReview(placeId: String) {
        val state = _state.value
        val reviewId = state.reviewId ?: return
        if (state.isSubmitting) return

        viewModelScope.launch {
            _state.value = state.copy(isSubmitting = true, errorMessage = null)
            val evaluatedScores = state.subScores.filter { it.value > 0.0 }

            when (val result = placeRepository.updateReview(
                reviewId = reviewId,
                placeId = placeId,
                mainScore = state.mainScore,
                comment = state.comment.trim(),
                scores = evaluatedScores
            )) {
                is ResultWrapper.Success -> {
                    _state.value = _state.value.copy(isSubmitting = false)
                    val reviewItem = result.data.data
                    if (reviewItem != null) {
                        val userSummary = UserReviewSummaryDto(
                            reviewId = reviewItem.id,
                            userName = reviewItem.name,
                            rating = reviewItem.rating,
                            text = reviewItem.content,
                            createdAt = reviewItem.createdAt,
                            updatedAt = reviewItem.updateAt,
                            scores = reviewItem.scores
                        )
                        _event.emit(AddReviewEvent.Updated(review = reviewItem, userSummary = userSummary))
                    }
                }
                is ResultWrapper.Error -> {
                    val errorMsg = result.message ?: "Yorum güncellenirken bir hata oluştu"
                    _state.value = _state.value.copy(
                        isSubmitting = false,
                        errorMessage = errorMsg
                    )
                    _event.emit(AddReviewEvent.Error(errorMsg))
                }
            }
        }
    }

    // 3. Yorum Silme (DELETE)
    fun deleteReview(placeId: String) {
        val state = _state.value
        val reviewId = state.reviewId ?: return
        val currentScore = state.mainScore
        if (state.isDeleting) return

        viewModelScope.launch {
            _state.value = state.copy(isDeleting = true, errorMessage = null)

            when (val result = placeRepository.deleteReview(reviewId = reviewId, placeId = placeId)) {
                is ResultWrapper.Success -> {
                    _state.value = _state.value.copy(isDeleting = false)
                    _event.emit(AddReviewEvent.Deleted(reviewId = reviewId, deletedScore = currentScore))
                }
                is ResultWrapper.Error -> {
                    val errorMsg = result.message ?: "Yorum silinirken bir hata oluştu"
                    _state.value = _state.value.copy(
                        isDeleting = false,
                        errorMessage = errorMsg
                    )
                    _event.emit(AddReviewEvent.Error(errorMsg))
                }
            }
        }
    }
}
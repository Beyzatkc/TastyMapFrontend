package org.beem.tastymap.review

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.place.model.review.UserReviewSummaryDto
import org.beem.tastymap.place.repository.PlaceRepository
import org.beem.tastymap.review.model.ScoreType

class AddReviewScreenModel(
    private val placeRepository: PlaceRepository
) : StateScreenModel<AddReviewState>(AddReviewState()) {

    private val _event = MutableSharedFlow<AddReviewEvent>(extraBufferCapacity = 1)
    val event = _event.asSharedFlow()

    fun setupReviewForm(existingReview: UserReviewSummaryDto?, defaultScore: Double = 0.0) {
        if (existingReview != null) {
            val scoreMap = existingReview.scores.associate { it.type to it.score }
            val hasAdvancedCriteria = scoreMap.keys.any {
                it !in listOf(ScoreType.TASTE, ScoreType.SERVICE, ScoreType.PRICE_PERFORMANCE)
            }

            mutableState.value = AddReviewState(
                isEditMode = true,
                reviewId = existingReview.reviewId,
                mainScore = existingReview.rating,
                subScores = scoreMap,
                comment = existingReview.text.orEmpty(),
                isAdvancedExpanded = hasAdvancedCriteria
            )
        } else {
            mutableState.value = AddReviewState(
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
            mutableState.value = mutableState.value.copy(mainScore = score)
        }
    }

    fun onMainScoreChange(score: Double) {
        mutableState.value = mutableState.value.copy(mainScore = score)
    }

    fun onCommentChange(comment: String) {
        mutableState.value = mutableState.value.copy(comment = comment)
    }

    fun onSubScoreChange(type: ScoreType, score: Double) {
        val currentScores = mutableState.value.subScores.toMutableMap()
        currentScores[type] = score
        mutableState.value = mutableState.value.copy(subScores = currentScores)
    }

    fun toggleAdvancedCriteria() {
        mutableState.value = mutableState.value.copy(
            isAdvancedExpanded = !mutableState.value.isAdvancedExpanded
        )
    }

    // 1. Yeni Yorum Gönderme (POST)
    fun submitReview(placeId: String) {
        val state = mutableState.value
        if (state.isSubmitting) return

        screenModelScope.launch {
            mutableState.value = state.copy(isSubmitting = true, errorMessage = null)
            val evaluatedScores = state.subScores.filter { it.value > 0.0 }

            when (val result = placeRepository.submitReview(
                placeId = placeId,
                mainScore = state.mainScore,
                comment = state.comment.trim(),
                scores = evaluatedScores
            )) {
                is ResultWrapper.Success -> {
                    mutableState.value = mutableState.value.copy(isSubmitting = false)
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
                    mutableState.value = mutableState.value.copy(
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
        val state = mutableState.value
        val reviewId = state.reviewId ?: return
        if (state.isSubmitting) return

        screenModelScope.launch {
            mutableState.value = state.copy(isSubmitting = true, errorMessage = null)
            val evaluatedScores = state.subScores.filter { it.value > 0.0 }

            when (val result = placeRepository.updateReview(
                reviewId = reviewId,
                placeId = placeId,
                mainScore = state.mainScore,
                comment = state.comment.trim(),
                scores = evaluatedScores
            )) {
                is ResultWrapper.Success -> {
                    mutableState.value = mutableState.value.copy(isSubmitting = false)
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
                    mutableState.value = mutableState.value.copy(
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
        val state = mutableState.value
        val reviewId = state.reviewId ?: return
        val currentScore = state.mainScore
        if (state.isDeleting) return

        screenModelScope.launch {
            mutableState.value = state.copy(isDeleting = true, errorMessage = null)

            when (val result = placeRepository.deleteReview(reviewId = reviewId, placeId = placeId)) {
                is ResultWrapper.Success -> {
                    mutableState.value = mutableState.value.copy(isDeleting = false)
                    _event.emit(AddReviewEvent.Deleted(reviewId = reviewId, deletedScore = currentScore))
                }
                is ResultWrapper.Error -> {
                    val errorMsg = result.message ?: "Yorum silinirken bir hata oluştu"
                    mutableState.value = mutableState.value.copy(
                        isDeleting = false,
                        errorMessage = errorMsg
                    )
                    _event.emit(AddReviewEvent.Error(errorMsg))
                }
            }
        }
    }
}
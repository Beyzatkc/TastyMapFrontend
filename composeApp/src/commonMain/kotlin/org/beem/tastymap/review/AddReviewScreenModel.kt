package org.beem.tastymap.review

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.beem.tastymap.place.repository.PlaceRepository
import org.beem.tastymap.review.model.ScoreType

class AddReviewScreenModel(
    private val placeRepository: PlaceRepository
) : StateScreenModel<AddReviewState>(AddReviewState()) {

    private val _event = MutableSharedFlow<AddReviewEvent>()
    val event = _event.asSharedFlow()

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

    fun submitReview(placeId: String) {
        val state = mutableState.value
        if (state.isSubmitting) return

        screenModelScope.launch {
            mutableState.value = state.copy(isSubmitting = true, errorMessage = null)
            try {
                val evaluatedScores = state.subScores.filter { it.value > 0.0 }
                placeRepository.submitReview(
                    placeId = placeId,
                    mainScore = state.mainScore,
                    comment = state.comment.trim(),
                    scores = evaluatedScores
                )
                _event.emit(AddReviewEvent.Success)
            } catch (e: Exception) {
                mutableState.value = mutableState.value.copy(
                    isSubmitting = false,
                    errorMessage = e.message ?: "Yorum gönderilirken bir hata oluştu"
                )
                _event.emit(AddReviewEvent.Error(e.message ?: "Hata"))
            }
        }
    }
}
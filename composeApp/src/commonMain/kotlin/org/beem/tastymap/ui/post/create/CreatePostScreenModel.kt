package org.beem.tastymap.ui.post.create
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.data.model.post.PostAndVisitRequest
import org.beem.tastymap.data.repository.PostRepository
import org.beem.tastymap.data.repository.VisitRepository
import org.jetbrains.compose.resources.StringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.error_explanation_limit
import tastymap.composeapp.generated.resources.error_photo_empty

class CreatePostScreenModel(
    private val visitRepository: VisitRepository,
    private val postRepository: PostRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow(CreatePostUiState())
    val uiState = _uiState.asStateFlow()

    sealed interface UiMessage {
        data class Dynamic(val message: String) : UiMessage
        data class Resource(val res: StringResource, val args: List<Any> = emptyList()) : UiMessage
    }

    init {
        loadInitialVisits()
    }

    fun loadInitialVisits() {
        screenModelScope.launch {
            _uiState.update { it.copy(isVisitsLoading = true, generalError = null) }

            val localVisits = visitRepository.getVisitsFlow().first()

            if (localVisits.isNotEmpty()) {
                _uiState.update {
                    it.copy(
                        visits = localVisits,
                        isVisitsLoading = false,
                        isLoadingMoreVisits = false,
                        visitsCurrentPage = 0
                    )
                }
            } else {
                fetchRemoteVisits(page = 0, isLoadMore = false)
            }
        }
    }

    fun loadMoreVisits() {
        val currentState = _uiState.value

        if (currentState.isVisitsLoading ||
            currentState.isLoadingMoreVisits ||
            currentState.isVisitsLastPage
        ) {
            return
        }

        screenModelScope.launch {
            _uiState.update { it.copy(isLoadingMoreVisits = true, loadMoreError = null) }
            val nextPage = currentState.visitsCurrentPage + 1
            fetchRemoteVisits(page = nextPage, isLoadMore = true)
        }
    }

    private suspend fun fetchRemoteVisits(page: Int, isLoadMore: Boolean) {
        when (val result = visitRepository.getPagedVisits(page = page)) {
            is ResultWrapper.Success -> {
                val pageResponse = result.data
                _uiState.update { state ->
                    val combinedList = if (isLoadMore) {
                        (state.visits + pageResponse.content).distinctBy { it.visitId }
                    } else {
                        pageResponse.content
                    }

                    state.copy(
                        visits = combinedList,
                        isVisitsLoading = false,
                        isLoadingMoreVisits = false,
                        loadMoreError = null,
                        isVisitsLastPage = pageResponse.last,
                        visitsCurrentPage = page
                    )
                }
            }
            is ResultWrapper.Error -> {
                _uiState.update { state ->
                    if (isLoadMore) {
                        state.copy(
                            isLoadingMoreVisits = false,
                            loadMoreError = result.message
                        )
                    } else {
                        state.copy(
                            isVisitsLoading = false,
                            generalError = result.message
                        )
                    }
                }
            }
        }
    }

    fun clearErrors() {
        _uiState.update { it.copy(explanationError = null, generalError = null) }
    }
    private fun validateExplanation(explanation: String?): Boolean {
        if (explanation != null && explanation.length > 500) {
            _uiState.update { it.copy(explanationError = Res.string.error_explanation_limit) }
            return false
        }
        _uiState.update { it.copy(explanationError = null) }
        return true
    }
    private fun validatePostPhoto(photoUrl: String?): Boolean {
        if (photoUrl == null || photoUrl.isEmpty() ) {
            _uiState.update { it.copy(photoError = Res.string.error_photo_empty) }
            return false
        }
        _uiState.update { it.copy(photoError = null) }
        return true
    }

    fun addPost(
        request: PostAndVisitRequest,
    ) {
        if (_uiState.value.isLoading) return

        if (!validateExplanation(request.explanation)) {
            return
        }
        if (!validatePostPhoto(request.photoUrl)) {
            return
        }

        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true, generalError = null) }

            when (val result = postRepository.addPost(request)) {
                is ResultWrapper.Success -> {
                    _uiState.update { it.copy(isLoading = false, success = true) }
                }
                is ResultWrapper.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            generalError = result.message
                        )
                    }
                }
            }
        }
    }
}
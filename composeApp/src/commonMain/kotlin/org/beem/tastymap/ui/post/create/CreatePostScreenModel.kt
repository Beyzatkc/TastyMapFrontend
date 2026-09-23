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
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.error_explanation_limit
import tastymap.composeapp.generated.resources.error_photo_empty
import tastymap.composeapp.generated.resources.error_photo_max_limit

class CreatePostScreenModel(
    private val visitRepository: VisitRepository,
    private val postRepository: PostRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow(CreatePostUiState())
    val uiState = _uiState.asStateFlow()

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
        _uiState.update {
            it.copy(
                explanationError = null,
                photoError = null,
                generalError = null
            )
        }
    }

    private fun validateExplanation(explanation: String?): Boolean {
        if (explanation != null && explanation.length > 500) {
            _uiState.update { it.copy(explanationError = Res.string.error_explanation_limit) }
            return false
        }
        _uiState.update { it.copy(explanationError = null) }
        return true
    }

    private fun validatePostPhoto(selectedImagesBytes: List<ByteArray>): Boolean {
        if (selectedImagesBytes.isEmpty()) {
            _uiState.update { it.copy(photoError = Res.string.error_photo_empty) }
            return false
        }
        if (selectedImagesBytes.size > 3) {
            _uiState.update { it.copy(photoError = Res.string.error_photo_max_limit) }
            return false
        }
        _uiState.update { it.copy(photoError = null) }
        return true
    }

    fun addPost(
        request: PostAndVisitRequest,
        selectedImagesBytes: List<ByteArray>
    ) {
        if (_uiState.value.isLoading) return

        // 1. Form Doğrulamaları
        val isExplanationValid = validateExplanation(request.explanation)
        val isPhotoValid = validatePostPhoto(selectedImagesBytes) // ByteArray listesi doğruluyor

        if (!isExplanationValid || !isPhotoValid) return

        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true, generalError = null) }

            // 2. Fotoğrafları Yükleme (ByteArray Listesi)
            var uploadedPhotoUrls = emptyList<String>()

            when (val uploadResult = postRepository.uploadPostPhotos(selectedImagesBytes)) {
                is ResultWrapper.Success -> {
                    uploadedPhotoUrls = uploadResult.data
                }

                is ResultWrapper.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            generalError = uploadResult.message
                        )
                    }
                    return@launch
                }
            }

            // 3. Yüklenen Fotoğraf URL'lerini Request'e Ekleme
            val finalRequest = request.copy(
                photoUrl = uploadedPhotoUrls
            )

            // 4. Gönderiyi Oluşturma
            when (val result = postRepository.addPost(finalRequest)) {
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
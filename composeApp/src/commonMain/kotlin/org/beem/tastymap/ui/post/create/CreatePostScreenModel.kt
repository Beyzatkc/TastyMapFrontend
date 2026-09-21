package org.beem.tastymap.ui.post.create
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.data.model.post.PostAndVisitRequest
import org.beem.tastymap.data.model.visit.VisitResponse
import org.beem.tastymap.data.repository.PostRepository
import org.beem.tastymap.data.repository.VisitRepository
class CreatePostScreenModel(
    private val visitRepository: VisitRepository,
    private val postRepository: PostRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow(CreatePostUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadVisits()
    }

    private fun loadVisits() {
        visitRepository.getVisitsFlow()
            .onEach { visitList ->
                _uiState.update { it.copy(visits = visitList) }
            }
            .launchIn(screenModelScope)

        screenModelScope.launch {
            visitRepository.syncVisits()
        }
    }

    fun clearErrors() {
        _uiState.update { it.copy(explanationError = null, generalError = null) }
    }

    private fun validateExplanation(explanation: String?): Boolean {
        if (explanation != null && explanation.length > 500) {
            _uiState.update { it.copy(explanationError = "Açıklama alanı en fazla 500 karakter olabilir.") }
            return false
        }
        _uiState.update { it.copy(explanationError = null) }
        return true
    }

    fun addPost(
        request: PostAndVisitRequest,
    ) {
        if (_uiState.value.isLoading) return

        if (!validateExplanation(request.explanation)) {
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
package org.beem.tastymap.ui.post.mypost.updatepost

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.data.model.post.PostResponse
import org.beem.tastymap.data.model.post.PostUpdateRequest
import org.beem.tastymap.data.repository.PostRepository
import org.beem.tastymap.ui.auth.common.CheckValidator
import org.beem.tastymap.ui.auth.common.ValidationResult
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.edit_post_success

class EditPostScreenModel(
    private val postRepository: PostRepository,
): ScreenModel {
    private val _uiState = MutableStateFlow(UpdatePostUiState())
    val uiState = _uiState.asStateFlow()

    private var observeJob: Job? = null
    private var initialPost: PostResponse? = null


    fun loadPostDetail(postId: Long) {
        observeJob?.cancel()

        observeJob = screenModelScope.launch {
            postRepository.getPostDetailStream(postId = postId).collect { post ->
                initialPost = post
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        post = post,
                    )
                }
            }
        }
    }

    private fun validateForm(explanation: String): Boolean {
        val uResult = CheckValidator.validateExplanation(explanation.trim())

        val explanationError = (uResult as? ValidationResult.Invalid)?.messageRes

        _uiState.update {
            it.copy(
               explanationError = explanationError
            )
        }

        return uResult is ValidationResult.Valid
    }

    fun updatePost(postId: Long, request: PostUpdateRequest) {
        val original = initialPost ?: return

        val changedExplanation = if(request.explanation?.trim() != original.explanation) request.explanation?.trim() else null
        val changedCommentEnabled = if(request.commentEnabled != original.isCommentEnabled) request.commentEnabled else null

        if (!validateForm(
                changedExplanation ?: original.username.orEmpty(),
            )
        ) return

        if (changedExplanation == null && changedCommentEnabled == null) {
            _uiState.update { it.copy(successMessageRes = Res.string.edit_post_success) }
            return
        }
        screenModelScope.launch {
            _uiState.update { it.copy(isActionLoading = true, errorMessage = null, successMessageRes = null) }

            val patchRequest = PostUpdateRequest(
                explanation = changedExplanation,
                commentEnabled =  changedCommentEnabled
            )

            when(val result = postRepository.updatePost(postId,patchRequest)){
                is ResultWrapper.Success ->  {
                    _uiState.update {
                        it.copy(
                            isActionLoading = false,
                            successMessageRes = Res.string.edit_post_success
                        )
                    }
                }
                is ResultWrapper.Error -> {
                    _uiState.update { it.copy(isActionLoading = false, errorMessage = result.message) }
                }
            }

        }
    }
}
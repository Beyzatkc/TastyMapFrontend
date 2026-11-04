package org.beem.tastymap.ui.post.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.data.repository.PostRepository

class PostDetailViewModel(
    private val postRepository: PostRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostDetailUiState())
    val uiState = _uiState.asStateFlow()

    private var observeJob: Job? = null

    fun isMe(userId: Long): Boolean {
        return postRepository.isMe(userId)
    }

    fun loadPostDetail(postId: Long) {
        observeJob?.cancel()
        _uiState.update {
            PostDetailUiState(isLoading = true)
        }

        observeJob = viewModelScope.launch {

            postRepository.getPostDetailStream(postId = postId).collect { post ->
                val isOwn = post?.userId == postRepository.myId
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        post = post,
                        isOwnPost = isOwn
                    )
                }
            }
        }
    }
    fun fetchRemotePost(postId: Long) {
        viewModelScope.launch {
            when (val result = postRepository.refreshPostDetail(postId)) {

                is ResultWrapper.Success -> {
                    _uiState.update {
                        it.copy(
                            isRefreshing = false,
                            isLoading = false,
                            isFetchPost = true,
                            errorMessage = null,
                        )
                    }
                }

                is ResultWrapper.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            isFetchPost = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }


    fun toggleLike(postId: Long) {
        viewModelScope.launch {
            val result = postRepository.toggleLike(postId)
            if (result is ResultWrapper.Error) {
                _uiState.update { it.copy(errorMessage = result.message) }
            }
        }
    }

    fun togglePin(postId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isActionLoadingPin = true) }
            val result = postRepository.togglePin(postId)

            when (result) {
                is ResultWrapper.Success -> {
                    _uiState.update { it.copy(isActionLoadingPin = false) }
                }
                is ResultWrapper.Error -> {
                    _uiState.update { it.copy(errorMessage = result.message, isActionLoadingPin = false) }
                }
            }
        }
    }

    fun deletePost(postId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isActionLoadingDelete = true) }
            when (val result = postRepository.deletePost(postId)) {
                is ResultWrapper.Success -> {
                    _uiState.update { it.copy(isDeletedSuccessfully = true, isActionLoadingDelete = false) }
                }
                is ResultWrapper.Error -> {
                    _uiState.update { it.copy(errorMessage = result.message, isActionLoadingDelete = false) }
                }
            }
        }
    }

    fun refreshPost(postId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            when (val result = postRepository.refreshPostDetail(postId)) {
                is ResultWrapper.Success -> {
                    _uiState.update { it.copy(isRefreshing = false) }
                }
                is ResultWrapper.Error -> {
                    _uiState.update { it.copy(errorMessage = result.message, isRefreshing = false) }
                }
            }
        }
    }
    fun resetState() {
        _uiState.update { PostDetailUiState() }
    }
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
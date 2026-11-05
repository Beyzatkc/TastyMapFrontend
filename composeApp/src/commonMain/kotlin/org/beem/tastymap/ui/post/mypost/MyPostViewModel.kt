package org.beem.tastymap.ui.post.mypost

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.data.repository.PostRepository
import org.beem.tastymap.ui.post.otherpost.PostListUiState
class MyPostViewModel(
    private val postRepository: PostRepository,
) : ViewModel() {
    private val pageSize = 12
    private val _uiState = MutableStateFlow(PostListUiState())
    val uiState = _uiState.asStateFlow()
    private var observeJob: Job? = null

    private var currentUserId: Long = 0L

    fun loadInitialData(userId: Long) {
        if (observeJob != null && currentUserId == userId) {
            return
        }
        currentUserId = userId
        observeJob?.cancel()

        _uiState.update { it.copy(isLoading = true) }

        observeJob = viewModelScope.launch {
            postRepository.getUserPostsStream(userId = userId, page = 0, size = pageSize)
                .catch { e ->
                    e.printStackTrace()
                    _uiState.update { it.copy( isLoadingMore = false,isLoading = false, errorMessage = e.message) }
                }
                .collect { posts ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            items = posts,
                            isLoading = false,
                            isRefreshing = false,
                            isLoadingMore = false
                        )
                    }
                }
        }
    }

    fun loadNextPage() {
        val currentState = _uiState.value
        if (currentUserId == 0L || currentState.isLoading || currentState.isLoadingMore || currentState.isLastPage) return

        _uiState.update { it.copy(isLoadingMore = true) }

        viewModelScope.launch {
            val nextPage = currentState.currentPage + 1

            val result = postRepository.fetchUserPostsPage(
                userId = currentUserId,
                page = nextPage,
                size = pageSize
            )

            when (result) {
                is ResultWrapper.Success -> {
                    val isLast = result.data.last ?: (result.data.content.size < pageSize)
                    _uiState.update {
                        it.copy(
                            currentPage = nextPage,
                            isLastPage = isLast,
                            isLoadingMore = false,
                            loadingMoreError = false
                        )
                    }
                }
                is ResultWrapper.Error -> {
                    _uiState.update {
                        it.copy(loadingMoreError = true, isLoadingMore = false, errorMessage = result.message)
                    }
                }
            }
        }
    }

    fun refresh() {
        if (_uiState.value.isRefreshing || currentUserId == 0L) return

        _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }

        viewModelScope.launch {
            val result = postRepository.fetchUserPostsPage(
                userId = currentUserId,
                page = 0,
                size = pageSize
            )

            if (result is ResultWrapper.Error) {
                _uiState.update { it.copy(isRefreshing = false, errorMessage = result.message) }
            } else {
                _uiState.update { it.copy(currentPage = 0, isRefreshing = false) }
            }
        }
    }
}
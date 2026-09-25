package org.beem.tastymap.ui.post.otherpost

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.data.repository.PostRepository


class PostScreenModel(
        private val postRepository: PostRepository) : ScreenModel {

        private val pageSize = 12
        private val _uiState = MutableStateFlow(PostListUiState())
        val uiState = _uiState.asStateFlow()

        private var observeJob: Job? = null
        private var currentUserId: Long = -1L


        fun loadInitialData(userId: Long) {
            if (currentUserId == userId && observeJob != null) return
            currentUserId = userId

            observeJob?.cancel() // Varsa eski kullanıcının dinleyicisini iptal et
            _uiState.update { PostListUiState(isLoading = true) }

            observeJob = screenModelScope.launch {
                postRepository.getUserPostsStream(userId = userId, page = 0, size = pageSize)
                    .catch { e ->
                        _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
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

        fun loadNextPage(userId: Long) {
            val currentState = _uiState.value
            if (currentState.isLoading || currentState.isLoadingMore || currentState.isLastPage) return

            _uiState.update { it.copy(isLoadingMore = true) }

            screenModelScope.launch {
                val nextPage = currentState.currentPage + 1
                val result = postRepository.fetchUserPostsPage(userId = userId, page = nextPage, size = pageSize)

                when (result) {
                    is ResultWrapper.Success -> {
                        val isLast = result.data.last ?: (result.data.content.size < pageSize)
                        _uiState.update {
                            it.copy(
                                currentPage = nextPage,
                                isLastPage = isLast,
                                isLoadingMore = false
                            )
                        }
                    }
                    is ResultWrapper.Error -> {
                        _uiState.update {
                            it.copy(isLoadingMore = false, errorMessage = result.message)
                        }
                    }
                }
            }
        }

        fun refresh(userId: Long) {
            if (_uiState.value.isRefreshing) return

            _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }

            screenModelScope.launch {
                val result = postRepository.fetchUserPostsPage(userId = userId, page = 0, size = pageSize)

                if (result is ResultWrapper.Error) {
                    _uiState.update { it.copy(isRefreshing = false, errorMessage = result.message) }
                } else {
                    _uiState.update { it.copy(currentPage = 0, isRefreshing = false) }
                }
            }
        }

}
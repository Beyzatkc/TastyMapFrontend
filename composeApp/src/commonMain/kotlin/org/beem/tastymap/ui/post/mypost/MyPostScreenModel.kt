package org.beem.tastymap.ui.post.mypost

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.data.model.post.PostUpdateRequest
import org.beem.tastymap.data.repository.PostRepository
import org.beem.tastymap.ui.post.otherpost.PostListUiState

class MyPostScreenModel(
    private val postRepository: PostRepository,
) : ScreenModel {
    private val pageSize = 12
    private val _uiState = MutableStateFlow(PostListUiState())
    val uiState = _uiState.asStateFlow()
    private var observeJob: Job? = null



    fun loadInitialData() {

        if (observeJob != null) {
            return
        }

        _uiState.update { it.copy(isLoading = true) }

        observeJob = screenModelScope.launch {
            postRepository.getMyPostsStream(page = 0, size = pageSize)
                .catch { e ->
                    e.printStackTrace()
                    _uiState.update { it.copy( isLoadingMore = false,isLoading = false, errorMessage = e.message) }
                }
                .collect { posts ->
                    posts.forEachIndexed { index, post ->
                        println("DEBUG_POST:   -> Post[$index]: ID=${post.postId}, Photo=${post.photoUrl}, Pinned=${post.isPinned}")
                    }

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

        if (currentState.isLoading || currentState.isLoadingMore || currentState.isLastPage) return

        _uiState.update { it.copy(isLoadingMore = true) }

        screenModelScope.launch {
            val nextPage = currentState.currentPage + 1
            val result = postRepository.fetchUserPostsPage(
                userId = postRepository.myId,
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
        if (_uiState.value.isRefreshing) return

        _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }

        screenModelScope.launch {
            val result = postRepository.fetchUserPostsPage(
                userId = postRepository.myId,
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



    //UPDATEYE BAKICLAK YANLIS SUAN BURDA OLCAYACAK
    fun updatePost(postId: Long, request: PostUpdateRequest) {
        screenModelScope.launch {
            val result = postRepository.updatePost(postId, request)
            if (result is ResultWrapper.Error) {
                _uiState.update { it.copy(errorMessage = result.message) }
            }
            // Başarılı olunca SQLite güncellenir ve Flow ekranı otomatik yeniler.
        }
    }


}
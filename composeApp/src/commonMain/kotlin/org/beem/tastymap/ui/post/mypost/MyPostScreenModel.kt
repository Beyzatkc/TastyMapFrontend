package org.beem.tastymap.ui.post.mypost

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.data.model.post.PostAndVisitRequest
import org.beem.tastymap.data.model.post.PostUpdateRequest
import org.beem.tastymap.data.repository.PostRepository
import org.beem.tastymap.ui.post.otherpost.PostListUiState

class MyPostScreenModel(
    private val postRepository: PostRepository,
) : ScreenModel {
    private val pageSize = 12
    private val _uiState = MutableStateFlow(PostListUiState())
    val uiState = _uiState.asStateFlow()



    fun loadInitialData() {
        _uiState.update { it.copy(isLoading = true) }
        fetchPage(page = 0, isRefresh = false)
    }

    fun loadNextPage() {
        val currentState = _uiState.value
        if (currentState.isLoading || currentState.isLoadingMore || currentState.isLastPage) return

        _uiState.update { it.copy(isLoadingMore = true) }
        fetchPage(page = currentState.currentPage + 1, isRefresh = false)
    }

    fun refresh() {
        if (_uiState.value.isRefreshing) return

        _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
        fetchPage(page = 0, isRefresh = true)
    }

    private fun fetchPage(page: Int, isRefresh: Boolean) {
        screenModelScope.launch {
            val result = postRepository.getMyPosts(page = page, size = pageSize, forceFetch = isRefresh)

            when (result) {
                is ResultWrapper.Success -> {
                    val pageData = result.data
                    val newItems = pageData.content
                    val isLast = pageData.last ?: (newItems.size < pageSize)

                    _uiState.update { currentState ->
                        val updatedList = if (isRefresh || page == 0) {
                            newItems
                        } else {
                            currentState.items + newItems
                        }

                        currentState.copy(
                            items = updatedList,
                            isLoading = false,
                            isRefreshing = false,
                            isLoadingMore = false,
                            currentPage = page,
                            isLastPage = isLast,
                            errorMessage = null
                        )
                    }
                }
                is ResultWrapper.Error -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            isRefreshing = false,
                            isLoadingMore = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    fun deletePost(postId: Long) {
        screenModelScope.launch {
            val result = postRepository.deletePost(postId)

            if (result is ResultWrapper.Success) {
                _uiState.update { currentState ->
                    currentState.copy(
                        items = currentState.items.filterNot { it.postId == postId }
                    )
                }
            } else if (result is ResultWrapper.Error) {
                _uiState.update { it.copy(errorMessage = result.message) }
            }
        }
    }

    //UPDATEYE BAKICLAK YANLIS SUAN BURDA OLCAYACAK
    fun updatePost(postId: Long, request: PostUpdateRequest) {
        screenModelScope.launch {
            val result = postRepository.updatePost(postId, request)

            if (result is ResultWrapper.Success) {

            } else if (result is ResultWrapper.Error) {
                _uiState.update { it.copy(errorMessage = result.message) }
            }
        }
    }

    fun togglePin(postId: Long) {
        screenModelScope.launch {
            val result = postRepository.togglePin(postId)

            if (result is ResultWrapper.Success) {
                val updatedPost = result.data

                _uiState.update { currentState ->
                    val updatedList = currentState.items.map { post ->
                        if (post.postId == postId) updatedPost else post
                    }

                    val sortedList = updatedList.sortedByDescending { it.isPinned }

                    currentState.copy(items = sortedList)
                }
            } else if (result is ResultWrapper.Error) {
                _uiState.update { it.copy(errorMessage = result.message) }
            }
        }
    }
}
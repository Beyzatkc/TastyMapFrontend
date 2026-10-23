package org.beem.tastymap.ui.post.postlike

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.data.repository.PostRepository
import org.beem.tastymap.domain.model.RelationStatus
import org.beem.tastymap.domain.usecase.ToggleFollowUseCase

class PostLikesViewModel(
    private val postRepository: PostRepository,
    private val toggleFollowUseCase: ToggleFollowUseCase,
) : ViewModel(){

    private val pageSize = 20
    private val _uiState = MutableStateFlow(PostLikesUiState())
    val uiState = _uiState.asStateFlow()

    fun isMe(userId: Long): Boolean {
        return postRepository.isMe(userId)
    }

    fun loadInitialData(postId: Long) {

        _uiState.update {
            PostLikesUiState(isLoading = true)
        }

        fetchPage(postId,page = 0)
    }

    fun loadNextPage(postId: Long) {
        val currentState = _uiState.value
        if (currentState.isLoading || currentState.isLoadingMore || currentState.isLastPage) return

        _uiState.update {
            it.copy(
                isLoadingMore = true,
                isLoadingMoreError = false
            )
        }
        fetchPage(postId, page = currentState.currentPage + 1)
    }


    private fun fetchPage(postId: Long, page: Int) {
        viewModelScope.launch {
            val result = postRepository.getPostLikes(postId)

            when (result) {
                is ResultWrapper.Success -> {
                    val pageData = result.data
                    val newItems = pageData.content
                    val isLast = pageData.last ?: (newItems.size < pageSize)

                    _uiState.update { currentState ->
                        val updatedList = if (page == 0) {
                            newItems
                        } else {
                            currentState.items + newItems
                        }

                        currentState.copy(
                            items = updatedList,
                            isLoading = false,
                            isLoadingMore = false,
                            isLoadingMoreError = false,
                            currentPage = page,
                            isLastPage = isLast,
                            errorMessage = null
                        )
                    }
                }
                is ResultWrapper.Error -> {
                    _uiState.update { currentState ->
                        if (page == 0) {
                            currentState.copy(
                                isLoading = false,
                                isLoadingMore = false,
                                errorMessage = result.message
                            )
                        } else {
                            currentState.copy(
                                isLoadingMore = false,
                                isLoadingMoreError = true,
                                errorMessage = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun handleFollowAction(targetUserId: Long, currentStatus: RelationStatus) {
        viewModelScope.launch {
            val action = ToggleFollowUseCase.Action.ToggleFollow(currentStatus)

            when (val result = toggleFollowUseCase(targetUserId, action)) {
                is ResultWrapper.Success -> {
                    val newRelationStatus = result.data.relationStatus

                    _uiState.update { currentState ->
                        val updatedItems = currentState.items.map { item ->
                            if (item.userId == targetUserId) {
                                item.copy(relationStatus = newRelationStatus)
                            } else {
                                item
                            }
                        }
                        currentState.copy(items = updatedItems)
                    }
                }
                is ResultWrapper.Error -> {
                    _uiState.update {
                        it.copy(errorMessage = result.message)
                    }
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

}
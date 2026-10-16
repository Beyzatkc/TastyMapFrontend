package org.beem.tastymap.ui.post.postcomment

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.beem.tastymap.core.local.UserManager
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.data.model.postcomment.CommentRequest
import org.beem.tastymap.data.repository.PostCommentRepository

class CommentScreenModel(
    private val repository: PostCommentRepository,
    private val userManager: UserManager
): ScreenModel {
    private val pageSize = 20
    private val _uiState = MutableStateFlow(CommentListUiState())
    val uiState = _uiState.asStateFlow()

    //YORUMUN UZUNLUK KONTROLU EKLENCEK
    fun addComment(postId: Long, contents: String, onSuccess: () -> Unit) {
        if (contents.isBlank()) return

        screenModelScope.launch {
            val request = CommentRequest(contents = contents.trim())
            val result = repository.addComment(postId, request)

            when (result) {
                is ResultWrapper.Success -> {
                    val newComment = result.data
                    _uiState.update { currentState ->
                        currentState.copy(
                            items = listOf(newComment) + currentState.items
                        )
                    }
                    onSuccess()
                }
                is ResultWrapper.Error -> {
                    _uiState.update { it.copy(errorMessage = result.message) }
                }
            }
        }
    }

    fun addReply(postId: Long, parentCommentId: Long, contents: String, onSuccess: () -> Unit) {
        if (contents.isBlank()) return

        screenModelScope.launch {
            val request = CommentRequest(contents = contents.trim())
            val result = repository.addReply(postId, request, parentCommentId)

            when (result) {
                is ResultWrapper.Success -> {
                    val newReply = result.data
                    _uiState.update { currentState ->
                        val currentReplyState = currentState.repliesState[parentCommentId] ?: ReplyState()
                        val updatedReplyMap = currentState.repliesState.toMutableMap()

                        updatedReplyMap[parentCommentId] = currentReplyState.copy(
                            items = listOf(newReply) + currentReplyState.items
                        )

                        val updatedItems = currentState.items.map { comment ->
                            if (comment.id == parentCommentId) {
                                comment.copy(replyCount = comment.replyCount + 1)
                            } else {
                                comment
                            }
                        }

                        currentState.copy(
                            items = updatedItems,
                            repliesState = updatedReplyMap,
                            expandedComments = currentState.expandedComments + parentCommentId
                        )
                    }
                    onSuccess()
                }
                is ResultWrapper.Error -> {
                    _uiState.update { it.copy(errorMessage = result.message) }
                }
            }
        }
    }
    fun getCurrentUserProfileUrl(): String? {
        return userManager.userSession.value?.profile
    }

    fun loadInitialData(postId: Long) {
        _uiState.update {
            CommentListUiState(isLoading = true)
        }
        fetchPage(postId, page = 0, isRefresh = false)
    }

    fun loadNextPage(postId: Long) {
        val currentState = _uiState.value
        if (currentState.isLoading || currentState.isLoadingMore || currentState.isLastPage) return

        _uiState.update { it.copy(isLoadingMore = true, loadingMoreError = false) }
        fetchPage(postId, page = currentState.currentPage + 1, isRefresh = false)
    }

    private fun fetchPage(postId: Long, page: Int, isRefresh: Boolean) {
        screenModelScope.launch {
            val result = repository.getComments(postId, page, pageSize)

            when (result) {
                is ResultWrapper.Success -> {
                    val pageData = result.data
                    val newItems = pageData.content
                    val isLast = pageData.last ?: (newItems.size < pageSize)

                    _uiState.update { currentState ->
                        val updatedList = if (isRefresh || page == 0) newItems else currentState.items + newItems
                        currentState.copy(
                            items = updatedList,
                            isLoading = false,
                            isLoadingMore = false,
                            currentPage = page,
                            isLastPage = isLast,
                            errorMessage = null,
                            loadingMoreError = false
                        )
                    }
                }
                is ResultWrapper.Error -> {
                    _uiState.update {
                        if (page == 0) {
                            it.copy(isLoading = false, isLoadingMore = false, errorMessage = result.message)
                        } else {
                            it.copy(isLoadingMore = false, loadingMoreError = true)
                        }
                    }
                }
            }
        }
    }

    fun toggleReplies(postId: Long, parentCommentId: Long) {
        val currentState = _uiState.value
        val isExpanded = currentState.expandedComments.contains(parentCommentId)

        if (isExpanded) {
            _uiState.update { it.copy(expandedComments = it.expandedComments - parentCommentId) }
        } else {
            _uiState.update { it.copy(expandedComments = it.expandedComments + parentCommentId) }

            val replyState = currentState.repliesState[parentCommentId]
            if (replyState == null || replyState.items.isEmpty()) {
                fetchReplies(postId, parentCommentId, page = 0)
            }
        }
    }

    fun loadNextRepliesPage(postId: Long, parentCommentId: Long) {
        val replyState = _uiState.value.repliesState[parentCommentId] ?: return
        if (replyState.isLoading || replyState.isLoadingMore || replyState.isLastPage) return

        fetchReplies(postId, parentCommentId, page = replyState.currentPage + 1)
    }

    private fun fetchReplies(postId: Long, parentCommentId: Long, page: Int) {
        _uiState.update { currentState ->
            val currentReplyState = currentState.repliesState[parentCommentId] ?: ReplyState()
            val updatedReplyMap = currentState.repliesState.toMutableMap()

            updatedReplyMap[parentCommentId] = currentReplyState.copy(
                isLoading = page == 0,
                isLoadingMore = page > 0,
                errorMessage = null,
                loadingMoreErrorReply = false
            )
            currentState.copy(repliesState = updatedReplyMap)
        }

        screenModelScope.launch {
            val result = repository.getReply(postId, parentCommentId, page, pageSize)

            _uiState.update { currentState ->
                val currentReplyState = currentState.repliesState[parentCommentId]!!
                val updatedReplyMap = currentState.repliesState.toMutableMap()

                when (result) {
                    is ResultWrapper.Success -> {
                        val newItems = result.data.content
                        val isLast = result.data.last ?: (newItems.size < pageSize)
                        val updatedList = if (page == 0) newItems else currentReplyState.items + newItems

                        updatedReplyMap[parentCommentId] = currentReplyState.copy(
                            items = updatedList,
                            currentPage = page,
                            isLastPage = isLast,
                            isLoading = false,
                            isLoadingMore = false,
                            loadingMoreErrorReply = false
                        )
                    }
                    is ResultWrapper.Error -> {
                        if (page == 0) {
                            updatedReplyMap[parentCommentId] = currentReplyState.copy(
                                isLoading = false,
                                isLoadingMore = false,
                                errorMessage = result.message
                            )
                        } else {
                            updatedReplyMap[parentCommentId] = currentReplyState.copy(
                                isLoadingMore = false,
                                loadingMoreErrorReply = true
                            )
                        }
                    }
                }
                currentState.copy(repliesState = updatedReplyMap)
            }
        }
    }
}

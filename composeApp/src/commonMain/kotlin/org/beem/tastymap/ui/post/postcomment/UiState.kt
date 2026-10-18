package org.beem.tastymap.ui.post.postcomment

import org.beem.tastymap.data.model.postcomment.CommentsResponse

data class ReplyState(
    val items: List<CommentsResponse> = emptyList(),
    val currentPage: Int = 0,
    val isLastPage: Boolean = false,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val errorMessage: String? = null,
    val loadingMoreErrorReply: Boolean = false
)

data class CommentListUiState(
    val items: List<CommentsResponse> = emptyList(),
    val repliesState: Map<Long, ReplyState> = emptyMap(),
    val expandedComments: Set<Long> = emptySet(),

    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val currentPage: Int = 0,
    val isLastPage: Boolean = false,
    val errorMessage: String? = null,
    val loadingMoreError: Boolean = false,
    val isActionLoadingDelete: Boolean = false,
)
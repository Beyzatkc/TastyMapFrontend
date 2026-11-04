package org.beem.tastymap.ui.post.postlike

import org.beem.tastymap.data.model.post.PostLikeUserResponse
import org.beem.tastymap.data.model.post.PostResponse

data class PostLikesUiState(
    val items: List<PostLikeUserResponse> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isLoadingMoreError: Boolean = false,
    val currentPage: Int = 0,
    val isLastPage: Boolean = false,
    val errorMessage: String? = null
)

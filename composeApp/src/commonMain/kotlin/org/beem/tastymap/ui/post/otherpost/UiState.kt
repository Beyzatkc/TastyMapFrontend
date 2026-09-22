package org.beem.tastymap.ui.post.otherpost

import org.beem.tastymap.data.model.post.PostGridResponse
import org.beem.tastymap.data.model.post.PostResponse

data class PostListUiState(
    val items: List<PostGridResponse> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val currentPage: Int = 0,
    val isLastPage: Boolean = false,
    val errorMessage: String? = null
)
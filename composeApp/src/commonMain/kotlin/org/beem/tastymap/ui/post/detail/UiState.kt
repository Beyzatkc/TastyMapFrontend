package org.beem.tastymap.ui.post.detail

import org.beem.tastymap.data.model.post.PostResponse

data class PostDetailUiState(
    val isLoading: Boolean = true,
    val isActionLoadingDelete: Boolean = false,
    val isActionLoadingPin: Boolean = false,
    val isRefreshing: Boolean = false,
    val post: PostResponse? = null,
    val isOwnPost: Boolean = false,
    val errorMessage: String? = null,
    val isDeletedSuccessfully: Boolean = false
)
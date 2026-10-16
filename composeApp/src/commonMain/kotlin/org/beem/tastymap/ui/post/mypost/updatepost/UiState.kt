package org.beem.tastymap.ui.post.mypost.updatepost
import org.beem.tastymap.data.model.post.PostResponse
import org.jetbrains.compose.resources.StringResource

data class UpdatePostUiState(
    val isLoading: Boolean = true,
    val isActionLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessageRes: StringResource? = null,
    val post: PostResponse? = null,
    val explanationError: StringResource? = null,
)
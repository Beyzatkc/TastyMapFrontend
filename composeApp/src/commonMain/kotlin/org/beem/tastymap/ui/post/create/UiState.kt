package org.beem.tastymap.ui.post.create

import org.beem.tastymap.data.model.visit.VisitResponse

data class CreatePostUiState(
    val visits: List<VisitResponse> = emptyList(),
    val isLoading: Boolean = false,
    val explanationError: String? = null,
    val generalError: String? = null,
    val success: Boolean = false,
)
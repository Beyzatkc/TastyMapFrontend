package org.beem.tastymap.ui.post.create

import org.beem.tastymap.data.model.visit.VisitResponse
import org.jetbrains.compose.resources.StringResource

data class CreatePostUiState(
    val visits: List<VisitResponse> = emptyList(),
    val isVisitsLoading: Boolean = false,
    val isLoadingMoreVisits: Boolean = false,
    val isVisitsLastPage: Boolean = false,
    val visitsCurrentPage: Int = 0,
    val isLoading: Boolean = false,
    val loadMoreError: String? = null,
    val success: Boolean = false,
    val explanationError: StringResource? = null,
    val photoError: StringResource? = null,
    val generalError: String? = null
)
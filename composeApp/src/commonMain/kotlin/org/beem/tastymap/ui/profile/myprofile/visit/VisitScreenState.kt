package org.beem.tastymap.ui.profile.myprofile.visit

import org.beem.tastymap.data.model.block.BlockResponse
import org.beem.tastymap.data.model.visit.VisitResponse

data class VisitScreenState(
    val items: List<VisitResponse> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isInitialLoadCompleted: Boolean = false,
    val currentPage: Int = 0,
    val isLastPage: Boolean = false,
    val errorMessage: String? = null
)
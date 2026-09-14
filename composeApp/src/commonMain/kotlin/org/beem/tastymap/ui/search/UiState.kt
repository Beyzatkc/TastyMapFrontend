package org.beem.tastymap.ui.search

import org.beem.tastymap.data.model.search.UserSearchResponse

data class SearchUiState(
    val query: String = "",
    val searchResults: List<UserSearchResponse> = emptyList(),
    val historyResults: List<UserSearchResponse> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val currentPage: Int = 0,
    val isLastPage: Boolean = false,
    val errorMessage: String? = null
) {

    val isHistoryMode: Boolean get() = query.trim().length < 2
}
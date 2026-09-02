package org.beem.tastymap.search.state

import org.beem.tastymap.search.model.SearchVenue

data class SearchUiState(
    val query: String = "",
    val results: List<SearchVenue> = emptyList(),
    val isLoading: Boolean = false,
    val isDropdownVisible: Boolean = false
)
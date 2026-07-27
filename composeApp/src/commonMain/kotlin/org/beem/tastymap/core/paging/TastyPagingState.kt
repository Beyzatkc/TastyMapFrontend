package org.beem.tastymap.core.paging

data class TastyPagingState<T>(
    val items: List<T> = emptyList(),
    val currentPage: Int = 0,
    val isLoading: Boolean = false,
    val isEndReached: Boolean = false,
    val error: String? = null
)
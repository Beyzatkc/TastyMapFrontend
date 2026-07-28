package org.beem.tastymap.core.paging

data class TastyPagingState<T>(
    val items: List<T> = emptyList(),
    val currentPage: Int = 0,
    val isLoading: Boolean = false,
    val isEndReached: Boolean = false,
    val error: String? = null
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TastyPagingState<*>) return false

        if (currentPage != other.currentPage) return false
        if (isLoading != other.isLoading) return false
        if (isEndReached != other.isEndReached) return false
        if (error != other.error) return false
        if (items.size != other.items.size) return false

        return items == other.items
    }

    override fun hashCode(): Int {
        var result = items.hashCode()
        result = 31 * result + currentPage
        result = 31 * result + isLoading.hashCode()
        result = 31 * result + isEndReached.hashCode()
        result = 31 * result + (error?.hashCode() ?: 0)
        return result
    }
}
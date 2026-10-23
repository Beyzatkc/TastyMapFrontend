package org.beem.tastymap.ui.profile.subscribers

import kotlinx.serialization.Serializable
import org.beem.tastymap.data.model.subscribers.SubscribeResponse

@Serializable
data class SubscribersListUiState(
    val items: List<SubscribeResponse> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val currentPage: Int = 0,
    val isLastPage: Boolean = false,
    val errorMessage: String? = null
)
@Serializable
enum class SubscriberListType {
    SUBSCRIBERS, // Takipçiler
    SUBSCRIBES   // Takip Edilenler
}
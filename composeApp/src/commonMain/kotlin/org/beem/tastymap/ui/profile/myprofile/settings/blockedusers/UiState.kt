package org.beem.tastymap.ui.profile.myprofile.settings.blockedusers

import org.beem.tastymap.data.model.block.BlockResponse
import org.beem.tastymap.data.model.subscribers.SubscribeResponse

data class BlockedUsersUiState(
    val items: List<BlockResponse> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val currentPage: Int = 0,
    val isLastPage: Boolean = false,
    val errorMessage: String? = null
)


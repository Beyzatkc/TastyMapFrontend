package org.beem.tastymap.ui.profile.myprofile.settings.blockedusers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.data.repository.BlockRepository
import org.beem.tastymap.domain.usecase.ToggleBlockUseCase

class BlockedViewModel(
    private val blockRepository: BlockRepository,
    private val toggleBlockUseCase: ToggleBlockUseCase,
): ViewModel() {
    private val _uiState = MutableStateFlow(BlockedUsersUiState())
    val uiState = _uiState.asStateFlow()

    private val pageSize = 10

    fun loadInitialData() {
        _uiState.update {
            BlockedUsersUiState(isLoading = true)
        }
        fetchPage(page = 0, isRefresh = false)
    }

    fun loadNextPage() {
        val currentState = _uiState.value
        if (currentState.isLoading || currentState.isLoadingMore || currentState.isLastPage) return

        _uiState.update { it.copy(isLoadingMore = true) }
        fetchPage(page = currentState.currentPage + 1, isRefresh = false)
    }

    fun refresh() {
        if (_uiState.value.isRefreshing) return

        _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
        fetchPage(page = 0, isRefresh = true)
    }

    private fun fetchPage(page: Int, isRefresh: Boolean) {
        viewModelScope.launch {
           val result = blockRepository.getBlockedUsers(page = page, size = pageSize,forceFetch = isRefresh )

            when (result) {
                is ResultWrapper.Success -> {
                    val pageData = result.data
                    val newItems = pageData.content
                    val isLast = pageData.last ?: (newItems.size < pageSize)

                    _uiState.update { currentState ->
                        val updatedList = if (isRefresh || page == 0) {
                            newItems
                        } else {
                            currentState.items + newItems
                        }

                        currentState.copy(
                            items = updatedList,
                            isLoading = false,
                            isRefreshing = false,
                            isLoadingMore = false,
                            currentPage = page,
                            isLastPage = isLast,
                            errorMessage = null
                        )
                    }
                }
                is ResultWrapper.Error -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            isRefreshing = false,
                            isLoadingMore = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }
    fun unblockUser(targetUserId: Long) {
        viewModelScope.launch {
            val result = toggleBlockUseCase(
                targetUserId = targetUserId,
                isCurrentlyBlocked = true
            )

            when (result) {
                is ResultWrapper.Success -> {
                    _uiState.update { currentState ->
                        val updatedList = currentState.items.filterNot { it.userId == targetUserId }
                        currentState.copy(items = updatedList)
                    }
                }
                is ResultWrapper.Error -> {
                    _uiState.update { currentState ->
                        currentState.copy(errorMessage = result.message)
                    }
                }
            }
        }
    }

}
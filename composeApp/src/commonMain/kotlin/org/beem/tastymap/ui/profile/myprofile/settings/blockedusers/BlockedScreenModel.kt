package org.beem.tastymap.ui.profile.myprofile.settings.blockedusers

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.data.repository.BlockRepository

class BlockedScreenModel(
    private val blockRepository: BlockRepository,
): ScreenModel {
    private val _uiState = MutableStateFlow(BlockedUsersUiState())
    val uiState = _uiState.asStateFlow()

    private val pageSize = 10

    fun loadInitialData(userId: Long) {
        _uiState.update {
            BlockedUsersUiState(isLoading = true)
        }
        fetchPage(userId,page = 0, isRefresh = false)
    }

    fun loadNextPage(userId: Long) {
        val currentState = _uiState.value
        if (currentState.isLoading || currentState.isLoadingMore || currentState.isLastPage) return

        _uiState.update { it.copy(isLoadingMore = true) }
        fetchPage(userId,page = currentState.currentPage + 1, isRefresh = false)
    }

    fun refresh(userId: Long) {
        if (_uiState.value.isRefreshing) return

        _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
        fetchPage(userId,page = 0, isRefresh = true)
    }

    private fun fetchPage(userId: Long,page: Int, isRefresh: Boolean) {
        screenModelScope.launch {
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

}
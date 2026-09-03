package org.beem.tastymap.ui.profile.subscribers

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.data.repository.SubscribersRepository

class SubscribersListScreenModel(
    private val subscribersRepository: SubscribersRepository,
) : ScreenModel {

    private val _uiState = MutableStateFlow(SubscribersListUiState())
    val uiState = _uiState.asStateFlow()

    private var currentListType: SubscriberListType = SubscriberListType.SUBSCRIBERS
    private val pageSize = 10


    fun loadInitialData(userId: Long, listType: SubscriberListType) {
        currentListType = listType

        _uiState.update {
            SubscribersListUiState(isLoading = true)
        }

        fetchPage(userId,page = 0, isRefresh = false)
    }


    fun loadNextPage(userId: Long) {
        val currentState = _uiState.value
        if (currentState.isLoading || currentState.isLoadingMore || currentState.isLastPage) return

        _uiState.update { it.copy(isLoadingMore = true) }
        fetchPage(userId,page = currentState.currentPage + 1, isRefresh = false)
    }

    /**
     * Pull-to-Refresh tetiklendiğinde veriyi yeniler
     */
    fun refresh(userId: Long) {
        if (_uiState.value.isRefreshing) return

        _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
        fetchPage(userId,page = 0, isRefresh = true)
    }

    private fun fetchPage(userId: Long,page: Int, isRefresh: Boolean) {
        screenModelScope.launch {
            val result = when (currentListType) {
                SubscriberListType.SUBSCRIBERS -> {
                    subscribersRepository.getUserSubscribers(userId = userId, page = page, size = pageSize)
                }
                SubscriberListType.SUBSCRIBES -> {
                    subscribersRepository.getUserSubscribes(userId = userId, page = page, size = pageSize)
                }
            }

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
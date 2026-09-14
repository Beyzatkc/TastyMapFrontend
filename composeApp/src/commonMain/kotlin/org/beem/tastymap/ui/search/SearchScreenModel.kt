package org.beem.tastymap.ui.search

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.beem.tastymap.core.local.UserManager
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.data.model.search.UserSearchResponse
import org.beem.tastymap.data.repository.SearchUserRepository

class SearchScreenModel(
    private val searchUserRepository: SearchUserRepository,
) : ScreenModel {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState = _uiState.asStateFlow()

    private val pageSize = 20
    private var searchJob: Job? = null


    init {
        _uiState.update { it.copy(isLoading = true) }
        observeHistory()
        fetchRemoteHistorySync()
    }

    fun isMe(userId: Long): Boolean {
        return searchUserRepository.isMe(userId)
    }
    private fun observeHistory() {
        searchUserRepository.getRecentSearchesFlow()
            .onEach { historyList ->
                _uiState.update { it.copy(historyResults = historyList,isLoading = false) }
            }
            .catch { e ->
                _uiState.update { it.copy(errorMessage = e.message,isLoading = false) }
            }
            .launchIn(screenModelScope)
    }

    private fun fetchRemoteHistorySync() {
        screenModelScope.launch {
            val result = searchUserRepository.getHistorySearchUsers()
            if (result is ResultWrapper.Error) {
                _uiState.update {
                    it.copy(errorMessage = result.message)
                }
            }
        }
    }

    fun onQueryChanged(newQuery: String) {
        _uiState.update { it.copy(query = newQuery, errorMessage = null) }

        searchJob?.cancel()

        if (newQuery.trim().length < 2) {
            _uiState.update {
                it.copy(
                    searchResults = emptyList(),
                    isLoading = false,
                    isLoadingMore = false,
                    currentPage = 0,
                    isLastPage = false
                )
            }
        } else {
            searchJob = screenModelScope.launch {
                delay(350)
                _uiState.update { it.copy(isLoading = true) }
                fetchSearchPage(page = 0, isRefresh = false)
            }
        }
    }

    fun clearQuery() {
        onQueryChanged("")
    }

    fun loadNextPage() {
        val currentState = _uiState.value
        if (currentState.isHistoryMode || currentState.isLoading || currentState.isLoadingMore || currentState.isLastPage) return

        _uiState.update { it.copy(isLoadingMore = true) }
        fetchSearchPage(page = currentState.currentPage + 1, isRefresh = false)
    }

    // refresh fonksiyonu güncellenmiş hali:
    fun refresh() {
        val currentState = _uiState.value
        if (currentState.isRefreshing) return

        _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }

        if (currentState.isHistoryMode) {
            screenModelScope.launch {
                val result = searchUserRepository.getHistorySearchUsers()
                _uiState.update { state ->
                    state.copy(
                        isRefreshing = false,
                        errorMessage = result.toString()
                    )
                }
            }
        } else {
            fetchSearchPage(page = 0, isRefresh = true)
        }
    }

    private fun fetchSearchPage(page: Int, isRefresh: Boolean) {
        val currentQuery = _uiState.value.query.trim()
        if (currentQuery.length < 2) return

        screenModelScope.launch {
            val result = searchUserRepository.getSearchUsers(
                keyword = currentQuery,
                page = page,
                size = pageSize
            )

            when (result) {
                is ResultWrapper.Success -> {
                    val newItems = result.data
                    val isLast = newItems.size < pageSize

                    _uiState.update { currentState ->
                        val updatedList = if (isRefresh || page == 0) {
                            newItems
                        } else {
                            currentState.searchResults + newItems
                        }

                        currentState.copy(
                            searchResults = updatedList,
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

    fun onUserClicked(user: UserSearchResponse) {
        screenModelScope.launch {
            searchUserRepository.addToHistory(user)
        }
    }

    fun deleteHistoryItem(userIdToDelete: Long) {
        screenModelScope.launch {
            searchUserRepository.deleteFromHistory(userIdToDelete)
        }
    }
}
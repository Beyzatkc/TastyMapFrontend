package org.beem.tastymap.ui.profile.myprofile.visit

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.data.repository.VisitRepository

class VisitScreenModel(
    private val repository: VisitRepository
) : ScreenModel {

    private val _state = MutableStateFlow(VisitScreenState())
    val state: StateFlow<VisitScreenState> = _state.asStateFlow()

    init {
        observeLocalVisits()
        syncWithServer(false)
    }

    private fun observeLocalVisits() {
        repository.getVisitsFlow()
            .onEach { visitList ->
                _state.update {
                    it.copy(
                        items = visitList,
                        errorMessage = null
                    )
                }
            }
            .catch { error ->
                _state.update {
                    it.copy(isLoading = false, errorMessage = error.message)
                }
            }
            .launchIn(screenModelScope)
    }


    fun syncWithServer(isPullToRefresh: Boolean = false) {
        if (!isPullToRefresh) {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
        } else {
            _state.update { it.copy(isRefreshing = true, errorMessage = null) }
        }

        screenModelScope.launch {
            when (val result = repository.syncVisits()) {
                is ResultWrapper.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            isLastPage = result.data.last,
                            isLoadingMore = false,
                            currentPage = if (!result.data.last) 1 else 0,
                            isInitialLoadCompleted = true
                        )
                    }
                }
                is ResultWrapper.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            isLoadingMore = false,
                            isInitialLoadCompleted = true,
                            errorMessage = result.message ?: "Bir hata oluştu"
                        )
                    }
                }
            }
        }
    }


    fun loadMore() {
        val currentState = _state.value

        if ( !currentState.isInitialLoadCompleted || currentState.isLoading || currentState.isLoadingMore || currentState.isLastPage) {
            return
        }

        screenModelScope.launch {
            _state.update { it.copy(isLoadingMore = true, errorMessage = null) }

            when (val result = repository.getPagedVisits(page = currentState.currentPage)) {
                is ResultWrapper.Success -> {
                    val pageResponse = result.data
                    _state.update { state ->
                        val combinedList = (state.items + pageResponse.content).distinctBy { it.visitId }

                        state.copy(
                            items = combinedList,
                            isLoadingMore = false,
                            isLastPage = pageResponse.last,
                            currentPage = if (!pageResponse.last) state.currentPage + 1 else state.currentPage
                        )
                    }
                }
                is ResultWrapper.Error -> {
                    _state.update {
                        it.copy(
                            isLoadingMore = false,
                            errorMessage = result.message ?: "Daha fazla veri yüklenemedi"
                        )
                    }
                }
            }
        }
    }

    fun deleteVisit(visitId: Long) {

        val previousItems = _state.value.items

        _state.update { state ->
            state.copy(
                items = state.items.filterNot { it.visitId == visitId }
            )
        }
        screenModelScope.launch {
            when (val result = repository.deleteVisit(visitId)) {
                is ResultWrapper.Success -> {

                }
                is ResultWrapper.Error -> {
                    _state.update {
                        it.copy(items = previousItems,errorMessage = result.message ?: "Silme işlemi başarısız")
                    }
                }
            }
        }
    }
    fun clearMessages() {
        _state.update {
            it.copy(
                errorMessage = null,
            )
        }
    }
}
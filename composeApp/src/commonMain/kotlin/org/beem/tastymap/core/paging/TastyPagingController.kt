package org.beem.tastymap.core.paging

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TastyPagingController<T>(
    private val pageSize: Int = 5,
    private val scope: CoroutineScope,
    private val fetchPage: suspend (page: Int, pageSize: Int) -> List<T>
) {
    private val _state = MutableStateFlow(TastyPagingState<T>())
    val state: StateFlow<TastyPagingState<T>> = _state.asStateFlow()

    private var activeJob: Job? = null

    fun loadNextPage() {
        val currentState = _state.value
        if (currentState.isLoading || currentState.isEndReached) return

        _state.update { it.copy(isLoading = true, error = null) }

        activeJob = scope.launch {
            try {
                val newItems = fetchPage(currentState.currentPage, pageSize)

                _state.update { prev ->
                    prev.copy(
                        items = prev.items + newItems,
                        currentPage = prev.currentPage + 1,
                        isLoading = false,
                        isEndReached = newItems.isEmpty() || newItems.size < pageSize
                    )
                }
            } catch (e: Exception) {
                _state.update { prev ->
                    prev.copy(
                        isLoading = false,
                        error = e.message ?: "Bilinmeyen bir hata oluştu"
                    )
                }
            }
        }
    }

    fun reset() {
        activeJob?.cancel()
        activeJob = null
        _state.value = TastyPagingState()
    }
}
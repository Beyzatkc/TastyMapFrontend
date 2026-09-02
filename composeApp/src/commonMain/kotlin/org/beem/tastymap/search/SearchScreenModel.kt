package org.beem.tastymap.search

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.search.model.SearchVenue
import org.beem.tastymap.search.repository.SearchRepository
import org.beem.tastymap.search.state.SearchEvent
import org.beem.tastymap.search.state.SearchIntent
import org.beem.tastymap.search.state.SearchUiState
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
class SearchScreenModel(
    private val searchRepository: SearchRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<SearchEvent>()
    val event = _event.asSharedFlow()

    private val searchQueryFlow = MutableStateFlow("")

    init {
        screenModelScope.launch {
            searchQueryFlow
                .debounce(350.milliseconds)
                .distinctUntilChanged()
                .collectLatest { query ->
                    if (query.trim().length < 2) {
                        _uiState.update { it.copy(results = emptyList(), isLoading = false, isDropdownVisible = false) }
                        return@collectLatest
                    }

                    _uiState.update { it.copy(isLoading = true) }
                    when (val result = searchRepository.searchVenues(query)) {
                        is ResultWrapper.Success -> {
                            _uiState.update {
                                it.copy(
                                    results = result.data,
                                    isLoading = false,
                                    isDropdownVisible = result.data.isNotEmpty()
                                )
                            }
                        }
                        is ResultWrapper.Error -> {
                            _uiState.update { it.copy(results = emptyList(), isLoading = false, isDropdownVisible = false) }
                        }
                    }
                }
        }
    }

    fun handleIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.QueryChanged -> {
                onQueryChanged(newQuery = intent.query)
            }
            is SearchIntent.ClearQuery -> {
                onClearQuery()
            }
            is SearchIntent.VenueClicked -> {
                onVenueClicked(intent.venue)
            }
            is SearchIntent.SearchTriggered -> {
                screenModelScope.launch {
                    _event.emit(SearchEvent.HideKeyboard)
                }
            }
        }
    }

    fun onQueryChanged(newQuery: String) {
        _uiState.update { it.copy(query = newQuery) }
        searchQueryFlow.value = newQuery
    }

    fun onVenueClicked(venue: SearchVenue) {
        screenModelScope.launch {
            _uiState.update { it.copy(query = "", results = emptyList(), isDropdownVisible = false) }
            _event.emit(SearchEvent.HideKeyboard)
            _event.emit(SearchEvent.VenueSelected(venue))
        }
    }

    fun onClearQuery() {
        _uiState.update { it.copy(query = "", results = emptyList(), isDropdownVisible = false) }
        searchQueryFlow.value = ""
    }
}
package org.beem.tastymap.search.state

import org.beem.tastymap.search.model.SearchVenue

sealed interface SearchIntent {
    data class QueryChanged(val query: String) : SearchIntent
    data object ClearQuery : SearchIntent
    data class VenueClicked(val venue: SearchVenue) : SearchIntent
    data object SearchTriggered : SearchIntent
}
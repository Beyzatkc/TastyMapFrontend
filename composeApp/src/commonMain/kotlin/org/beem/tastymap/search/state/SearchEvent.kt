package org.beem.tastymap.search.state

import org.beem.tastymap.search.model.SearchVenue

sealed class SearchEvent {
    data class VenueSelected(val venue: SearchVenue) : SearchEvent()
    data object HideKeyboard : SearchEvent()
}
package org.beem.tastymap.search.model

import kotlinx.serialization.Serializable

@Serializable
data class SearchData(
    val venues: List<SearchVenue> = emptyList()
    // users listesini modellemiyoruz, Kotlinx Serialization otomatik ignore eder
)
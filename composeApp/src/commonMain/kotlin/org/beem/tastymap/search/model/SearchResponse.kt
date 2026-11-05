package org.beem.tastymap.search.model

import kotlinx.serialization.Serializable

@Serializable
data class SearchResponse(
    val success: Boolean,
    val message: String,
    val data: SearchData,
    val timestamp: String? = null,
    val errorCode: String? = null
)
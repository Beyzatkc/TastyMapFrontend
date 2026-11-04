package org.beem.tastymap.data.model.search

import kotlinx.serialization.Serializable

@Serializable
data class UserSearchResponse(
    val id: Long,
    val username: String,
    val name: String,
    val profile: String?,
    val searchedAt: String? = null
)
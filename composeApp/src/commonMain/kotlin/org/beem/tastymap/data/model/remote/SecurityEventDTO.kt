package org.beem.tastymap.data.model.remote

import kotlinx.serialization.Serializable

@Serializable
data class SecurityEventDTO(
    val type: String,
    val message: String
)
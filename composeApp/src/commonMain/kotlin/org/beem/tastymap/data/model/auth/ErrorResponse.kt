package org.beem.tastymap.data.model.auth

import kotlinx.serialization.Serializable


@Serializable
data class ErrorResponse(
    val status: Int? = null,
    val error: String? = null,
    val message: String? = null,
    val email: String? = null
)
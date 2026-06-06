package org.beem.tastymap.data.model

import kotlinx.serialization.Serializable


@Serializable
data class ErrorResponse(val message: String? = null)
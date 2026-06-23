package org.beem.tastymap.data.model

import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?,
    val timestamp: String,
    val errorCode: String?
)
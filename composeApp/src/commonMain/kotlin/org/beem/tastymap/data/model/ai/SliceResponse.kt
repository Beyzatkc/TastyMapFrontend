package org.beem.tastymap.data.model.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SliceResponse<T>(
    @SerialName("content") val content: List<T> = emptyList(),
    @SerialName("number") val number: Int = 0,
    @SerialName("size") val size: Int = 20,
    @SerialName("first") val first: Boolean = true,
    @SerialName("last") val last: Boolean = false,
    @SerialName("empty") val empty: Boolean = false,
    @SerialName("numberOfElements") val numberOfElements: Int = 0
)
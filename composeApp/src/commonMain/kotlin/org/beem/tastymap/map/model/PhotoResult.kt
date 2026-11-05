package org.beem.tastymap.map.model

import kotlinx.serialization.Serializable

@Serializable
data class PhotoResult(
    val photo_reference: String,
    val width: Int,
    val height: Int
)
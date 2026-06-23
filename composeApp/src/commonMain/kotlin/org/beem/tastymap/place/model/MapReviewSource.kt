package org.beem.tastymap.place.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable(with = MapReviewSourceSerializer::class)
enum class MapReviewSource {
    @SerialName("GOOGLE")
    GOOGLE,
    @SerialName("INTERNAL")
    INTERNAL,
    UNKNOWN
}
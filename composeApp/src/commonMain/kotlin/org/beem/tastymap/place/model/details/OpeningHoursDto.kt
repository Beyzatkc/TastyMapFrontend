package org.beem.tastymap.place.model.details

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class OpeningHoursDto(
    @SerialName("open_now")
    val openNow: Boolean? = null,
    @SerialName("weekday_text")
    val weekdayText: List<String> = emptyList()
)
package org.beem.tastymap.map.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import org.beem.tastymap.ui.components.TastyMapIcon

@Serializable
data class Properties(
    @SerialName("min_zoom")
    val minZoom: Double,
    val name: String,
    val rating: Double?,
    val id: String,
    val category: String,
    val priority: Double,
    val status: String,
    @SerialName("icon_to_use")
    val iconToUse: String = TastyMapIcon.DEFAULT.iconName,
    @SerialName("icon_scale")
    val iconScale: Double = TastyMapIcon.DEFAULT.iconScale
)

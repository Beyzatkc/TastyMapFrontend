package org.beem.tastymap.data.model.device

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationPreferenceRequest(
    @SerialName("deviceId")
    val deviceId: String,

    @SerialName("isEnabled")
    val isEnabled: Boolean
)
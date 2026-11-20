package org.beem.tastymap.data.model.device

import kotlinx.serialization.Serializable

@Serializable
data class FcmTokenUpdateRequest(
    val deviceId: String,
    val fcmToken: String
)
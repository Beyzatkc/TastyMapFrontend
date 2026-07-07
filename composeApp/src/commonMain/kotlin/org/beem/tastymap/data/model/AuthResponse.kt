package org.beem.tastymap.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
data class RefreshTokenResponseDTO(
    val accessToken: String,
    val refreshToken: String,
    val message: String,
)
@Serializable
data class ApprovedRefreshRequestDTO(
    val deviceId: String,
    val userAgent: String,
    val fcmToken: String
)
@Serializable
enum class Status {
    PENDING,
    APPROVED,
    REJECTED,
    EXPIRED
}

@Serializable
data class NotificationResponse(
    val status: Status? = null,
    @SerialName("used")
    val isUsed: Boolean? = false
)

@Serializable
data class CommonRequest(
    val deviceId: String,
    val email: String
)
@Serializable
data class ResetPassword(
    val token: String,
    val newPassword: String
)


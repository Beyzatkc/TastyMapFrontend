package org.beem.tastymap.data.model.profile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponse(
    val username: String,
    val name: String,
    val profile: String,
    val role: String,
    val biography: String,
    val postCount: Long,
    val subscriberCount: Long,
    val subscribedCount: Long,
    val blockedByMe: Boolean = false,
    val blockedMe: Boolean = false
)

@Serializable
data class ActiveDevicesResponse(
    val activeDeviceCount: Long,
    val devices: List<ActiveDeviceDTO>
)
@Serializable
data class ActiveDeviceDTO(
    val deviceId: String,
    val userAgent: String,
    val city: String?,
    val lastUsedAt: String
)
@Serializable
data class RefreshTokenRequest(
    val refreshToken: String,
    val deviceId: String
)

@Serializable
data class UpdateProfile(
    val username: String,
    val name: String,
    val surname: String,
    val profilePhoto: String? = null,
    val biography: String? = null,
    @SerialName("isPrivate")
    val isPrivate: Boolean
)
@Serializable
data class MessageResponse(
    val message: String
)
@Serializable
data class ChangePassword(
    val oldPassword: String,
    val newPassword: String,
    val againNew: String,
    val deviceId: String
)
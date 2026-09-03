package org.beem.tastymap.data.model.profile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.beem.tastymap.domain.model.RelationStatus

@Serializable
data class ProfileResponse(
    val username: String,
    val name: String,
    val surname: String,
    val profile: String? = null,
    val role: String? = null,
    val biography: String? = null,
    val postCount: Long,
    val subscriberCount: Long,
    val subscribedCount: Long,
    val blockedByMe: Boolean = false,
    val blockedMe: Boolean = false,
    val relationStatus: RelationStatus = RelationStatus.NOT_FOLLOWING,
    val hasPendingIncomingRequest: Boolean = false

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
data class UpdateProfile(
    val username: String? = null,
    val name: String? = null,
    val surname: String? = null,
    val profilePhoto: String? = null,
    val biography: String? = null
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
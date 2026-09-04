package org.beem.tastymap.data.model.profile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.beem.tastymap.domain.model.RelationStatus

@Serializable
data class ProfileResponse(
    @SerialName("username")
    val username: String,

    @SerialName("name")
    val name: String,

    @SerialName("surname")
    val surname: String,

    @SerialName("profile")
    val profile: String? = null,

    @SerialName("role")
    val role: String? = null,

    @SerialName("biography")
    val biography: String? = null,

    @SerialName("postCount")
    val postCount: Long = 0,

    @SerialName("subscriberCount")
    val subscriberCount: Long = 0,

    @SerialName("subscribedCount")
    val subscribedCount: Long = 0,

    @SerialName("blockedByMe")
    val blockedByMe: Boolean = false,

    @SerialName("blockedMe")
    val blockedMe: Boolean = false,

    @SerialName("relationStatus")
    val relationStatus: RelationStatus = RelationStatus.NOT_FOLLOWING,

    @SerialName("hasPendingIncomingRequest")
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
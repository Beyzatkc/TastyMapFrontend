package org.beem.tastymap.domain.model

import kotlinx.serialization.SerialName

data class UserProfile(
    val userId: Long,
    val username: String,
    val name: String,
    val surname: String,
    val profilePhoto: String?,
    val role: String?,
    val biography: String?,
    val postCount: Long,
    val subscriberCount: Long,
    val subscribedCount: Long,
    val blockedByMe: Boolean = false,
    val blockedMe: Boolean = false,
    val relationStatus: RelationStatus = RelationStatus.NOT_FOLLOWING,
    val hasPendingIncomingRequest: Boolean = false
)

enum class RelationStatus {
    @SerialName("SELF")
    SELF,

    @SerialName("FOLLOWING")
    FOLLOWING,

    @SerialName("PENDING")
    PENDING,

    @SerialName("FOLLOW_BACK")
    FOLLOW_BACK,

    @SerialName("NOT_FOLLOWING")
    NOT_FOLLOWING
}
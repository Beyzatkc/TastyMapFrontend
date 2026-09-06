package org.beem.tastymap.data.model.socialnotifications

import kotlinx.serialization.Serializable
import org.beem.tastymap.domain.model.RelationStatus

@Serializable
enum class SocialNotificationType {
    FOLLOW_REQUEST,
    FOLLOW_ACCEPTED,
    NEW_FOLLOWER,
    POST_LIKE,
    COMMENT
}
@Serializable
enum class NotificationActionStatus {
    NONE,
    PENDING,
    ACCEPTED,
    REJECTED
}

@Serializable
data class SocialNotificationsResponse(
    val id: Long,
    val type: SocialNotificationType,
    val actionStatus: NotificationActionStatus,
    val isRead: Boolean,
    val createdAt: String,
    val actor: ActorDTO,
    val target: TargetDTO?
) {
    @Serializable
    data class ActorDTO(
        val id: Long,
        val username: String,
        val profilePhotoUrl: String?,
        val relationStatus: RelationStatus?
    )

    @Serializable
    data class TargetDTO(
        val targetId: Long?,
        val mediaUrl: String?,
        val content: String?
    )
}
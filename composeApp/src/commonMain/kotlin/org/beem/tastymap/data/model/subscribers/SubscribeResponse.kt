package org.beem.tastymap.data.model.subscribers

import kotlinx.serialization.Serializable
import org.beem.tastymap.domain.model.RelationStatus

@Serializable
enum class SubscribeStatus {
    PENDING,
    ACCEPTED
}
@Serializable
data class SubscribeResponse(
    val id: Long,
    val profile: String? = null,
    val username: String,
    val relationStatus: RelationStatus? = null // nullgelırse takıp eıdlmıyor demek
)

@Serializable
data class SubscribeActionResult(
    val targetUserId: Long,
    val relationStatus: RelationStatus,
    val hasPendingIncomingRequest: Boolean,
)
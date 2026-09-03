package org.beem.tastymap.data.model.subscribers

import kotlinx.serialization.Serializable

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
    val relationStatus: SubscribeStatus? = null // nullgelırse takıp eıdlmıyor demek
)
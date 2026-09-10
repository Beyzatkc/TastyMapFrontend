package org.beem.tastymap.data.model.block
import kotlinx.serialization.Serializable

@Serializable
data class BlockResponse(
    val userId: Long,
    val username: String? = null,
    val profilephoto: String,
    val blockedAt: String
)
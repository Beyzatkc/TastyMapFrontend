package org.beem.tastymap.domain.model
data class UserProfile(
    val userId: Long,
    val username: String,
    val name: String,
    val profilePhoto: String?,
    val role: String?,
    val biography: String?,
    val postCount: Long,
    val subscriberCount: Long,
    val subscribedCount: Long,
    val blockedByMe: Boolean = false,
    val blockedMe: Boolean = false
)
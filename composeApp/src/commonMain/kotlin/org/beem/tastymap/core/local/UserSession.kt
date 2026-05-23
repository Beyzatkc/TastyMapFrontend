package org.beem.tastymap.core.local

data class UserSession (
    val status: String?,
    val message: String?,
    val userId: Long?,
    val username: String?,
    val name: String?,
    val surname: String?,
    val profile: String?,
    val role: String?,
    val date: String?,
    val biography: String?
)
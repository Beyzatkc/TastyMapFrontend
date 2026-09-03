package org.beem.tastymap.domain.model
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
    SELF,           // Kendi profilim
    FOLLOWING,      // Takip ediyorsun
    PENDING,        // Takip isteği gönderildi, onay bekleniyor
    FOLLOW_BACK,    // O seni takip ediyor, sen etmiyorsun
    NOT_FOLLOWING   // Takip ilişkisi yok
}
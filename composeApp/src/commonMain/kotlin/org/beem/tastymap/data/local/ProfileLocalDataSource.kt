package org.beem.tastymap.data.local

import org.beem.tastymap.domain.model.UserProfile
import org.beem.tastymap.sqldelight.ProfileEntityQueries
import kotlin.time.Clock

class ProfileLocalDataSource(private val queries: ProfileEntityQueries) {

    fun getProfile(userId: Long): UserProfile? {
        val entity = queries.getProfileById(userId).executeAsOneOrNull() ?: return null
        return UserProfile(
            userId = entity.userId,
            username = entity.username,
            name = entity.name,
            profilePhoto = entity.profilePhoto,
            role = entity.role,
            biography = entity.biography,
            postCount = entity.postCount,
            subscriberCount = entity.subscriberCount,
            subscribedCount = entity.subscribedCount,
            blockedByMe = entity.blockedByMe == 1L,
            blockedMe = entity.blockedMe == 1L
        )
    }

    fun saveProfile(profile: UserProfile) {
        queries.insertOrUpdateProfile(
            userId = profile.userId,
            username = profile.username,
            name = profile.name,
            profilePhoto = profile.profilePhoto,
            role = profile.role,
            biography = profile.biography,
            postCount = profile.postCount,
            subscriberCount = profile.subscriberCount,
            subscribedCount = profile.subscribedCount,
            blockedByMe = if (profile.blockedByMe) 1L else 0L,
            blockedMe = if (profile.blockedMe) 1L else 0L,
            updatedAt = Clock.System.now().toEpochMilliseconds()
        )
        queries.trimOldProfiles()
    }

    fun deleteProfile(userId: Long) {
        queries.deleteProfileById(userId)
    }

    fun clearAll() {
        queries.clearAllProfiles()
    }

    fun updateCounts(userId: Long, subscriberCount: Long, subscribedCount: Long, postCount: Long) {
        queries.updateCounts(
            subscriberCount = subscriberCount,
            subscribedCount = subscribedCount,
            postCount = postCount,
            updatedAt = Clock.System.now().toEpochMilliseconds(),
            userId = userId
        )
    }

    fun incrementSubscriber(userId: Long) {
        queries.incrementSubscriberCount(
            updatedAt = Clock.System.now().toEpochMilliseconds(),
            userId = userId
        )
    }

    fun decrementSubscriber(userId: Long) {
        queries.decrementSubscriberCount(
            updatedAt = Clock.System.now().toEpochMilliseconds(),
            userId = userId
        )
    }
}
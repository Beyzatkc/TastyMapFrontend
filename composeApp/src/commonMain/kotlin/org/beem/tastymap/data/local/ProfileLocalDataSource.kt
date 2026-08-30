package org.beem.tastymap.data.local

import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import org.beem.tastymap.domain.model.UserProfile
import org.beem.tastymap.sqldelight.ProfileEntityQueries
import kotlin.time.Clock

class ProfileLocalDataSource(private val queries: ProfileEntityQueries) {

    // SELECT sorgularında awaitAsOneOrNull() kullanılmaya devam eder
    suspend fun getProfile(userId: Long): UserProfile? {
        val entity = queries.getProfileById(userId).awaitAsOneOrNull() ?: return null
        return UserProfile(
            userId = entity.userId,
            username = entity.username,
            name = entity.name,
            surname = entity.surname,
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

    // INSERT / UPDATE / DELETE sorguları zaten suspend fonksiyondur, .await() yazmayın
    suspend fun saveProfile(profile: UserProfile) {
        queries.insertOrUpdateProfile(
            userId = profile.userId,
            username = profile.username,
            name = profile.name,
            surname = profile.surname,
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

    suspend fun deleteProfile(userId: Long) {
        queries.deleteProfileById(userId)
    }

    suspend fun clearAll() {
        queries.clearAllProfiles()
    }

    suspend fun updateCounts(userId: Long, subscriberCount: Long, subscribedCount: Long, postCount: Long) {
        queries.updateCounts(
            subscriberCount = subscriberCount,
            subscribedCount = subscribedCount,
            postCount = postCount,
            updatedAt = Clock.System.now().toEpochMilliseconds(),
            userId = userId
        )
    }

    suspend fun incrementSubscriber(userId: Long) {
        queries.incrementSubscriberCount(
            updatedAt = Clock.System.now().toEpochMilliseconds(),
            userId = userId
        )
    }

    suspend fun decrementSubscriber(userId: Long) {
        queries.decrementSubscriberCount(
            updatedAt = Clock.System.now().toEpochMilliseconds(),
            userId = userId
        )
    }
}
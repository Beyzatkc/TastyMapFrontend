package org.beem.tastymap.data.local

import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.beem.tastymap.domain.model.UserProfile
import org.beem.tastymap.sqldelight.ProfileEntityQueries
import kotlin.time.Clock

class ProfileLocalDataSource(private val queries: ProfileEntityQueries) {

    suspend fun getProfile(userId: Long): UserProfile? = withContext(Dispatchers.Default) {
        val entity = queries.getProfileById(userId).awaitAsOneOrNull() ?: return@withContext null
        UserProfile(
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

    suspend fun updatePartialProfile(
        userId: Long,
        username: String?,
        name: String?,
        surname: String?,
        profilePhoto: String?,
        biography: String?
    ) {
        queries.updatePartialProfile(
            userId = userId,
            username = username,
            name = name,
            surname = surname,
            profilePhoto = profilePhoto,
            biography = biography
        )
    }
    suspend fun saveProfile(profile: UserProfile) = withContext(Dispatchers.Default) {
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

    suspend fun deleteProfile(userId: Long) = withContext(Dispatchers.Default) {
        queries.deleteProfileById(userId)
    }

    suspend fun clearAll() = withContext(Dispatchers.Default) {
        queries.clearAllProfiles()
    }

    suspend fun updateCounts(userId: Long, subscriberCount: Long, subscribedCount: Long, postCount: Long) = withContext(Dispatchers.Default) {
        queries.updateCounts(
            subscriberCount = subscriberCount,
            subscribedCount = subscribedCount,
            postCount = postCount,
            updatedAt = Clock.System.now().toEpochMilliseconds(),
            userId = userId
        )
    }

    suspend fun incrementSubscriber(userId: Long) = withContext(Dispatchers.Default) {
        queries.incrementSubscriberCount(
            updatedAt = Clock.System.now().toEpochMilliseconds(),
            userId = userId
        )
    }

    suspend fun decrementSubscriber(userId: Long) = withContext(Dispatchers.Default) {
        queries.decrementSubscriberCount(
            updatedAt = Clock.System.now().toEpochMilliseconds(),
            userId = userId
        )
    }
}
package org.beem.tastymap.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.beem.tastymap.domain.model.RelationStatus
import org.beem.tastymap.domain.model.UserProfile
import org.beem.tastymap.sqldelight.ProfileEntityQueries
import kotlin.time.Clock

class ProfileLocalDataSource(private val queries: ProfileEntityQueries) {

    fun getProfileFlow(userId: Long): Flow<UserProfile?> {
        return queries.getProfileById(userId)
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { entity ->
                entity?.let {
                    UserProfile(
                        userId = it.userId,
                        username = it.username,
                        name = it.name,
                        surname = it.surname,
                        profilePhoto = it.profilePhoto,
                        role = it.role,
                        biography = it.biography,
                        postCount = it.postCount,
                        subscriberCount = it.subscriberCount,
                        subscribedCount = it.subscribedCount,
                        blockedByMe = it.blockedByMe == 1L,
                        blockedMe = it.blockedMe == 1L,
                        relationStatus = try {
                            RelationStatus.valueOf(it.relationStatus)
                        } catch (e: Exception) {
                            RelationStatus.NOT_FOLLOWING
                        },
                        hasPendingIncomingRequest = it.hasPendingIncomingRequest == 1L,
                        isFollower = it.isFollower == 1L,
                        privateProfile = it.privateProfile == 1L
                    )
                }
            }
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
            relationStatus = profile.relationStatus.name,
            hasPendingIncomingRequest = if (profile.hasPendingIncomingRequest) 1L else 0L,
            isFollower = if (profile.isFollower) 1L else 0L,
            privateProfile = if (profile.privateProfile) 1L else 0L,
            updatedAt = Clock.System.now().toEpochMilliseconds()
        )

        queries.trimOldProfiles()
    }

    suspend fun deleteProfile(userId: Long) = withContext(Dispatchers.Default) {
        queries.deleteProfileById(userId)
    }
    suspend fun updatePrivacyStatus(userId: Long, isPrivate: Boolean) = withContext(Dispatchers.Default) {
        val privateProfileValue = if (isPrivate) 1L else 0L

        queries.updatePrivacyStatus(
            privateProfile = privateProfileValue,
            updatedAt = Clock.System.now().toEpochMilliseconds(),
            userId = userId
        )
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

    suspend fun decrementSubscribed(userId: Long) = withContext(Dispatchers.Default) {
        queries.decrementSubscribedCount(
            updatedAt = Clock.System.now().toEpochMilliseconds(),
            userId = userId
        )
    }

    suspend fun incrementSubscribed(userId: Long) = withContext(Dispatchers.Default) {
        queries.incrementSubscribedCount(
            updatedAt = Clock.System.now().toEpochMilliseconds(),
            userId = userId
        )
    }
    suspend fun blockUserInLocal(userId: Long) = withContext(Dispatchers.Default) {
        queries.blockUserUpdate(
            updatedAt = Clock.System.now().toEpochMilliseconds(),
            userId = userId
        )
    }

    suspend fun unblockUserInLocal(userId: Long) = withContext(Dispatchers.Default) {
        queries.unblockUserUpdate(
            updatedAt = Clock.System.now().toEpochMilliseconds(),
            userId = userId
        )
    }

    suspend fun updateRelationStatus(
        userId: Long,
        relationStatus: RelationStatus,
        hasPendingIncomingRequest: Boolean,
        isFollower: Boolean
    ) = withContext(Dispatchers.Default) {
        queries.updateRelationStatus(
            relationStatus = relationStatus.name,
            hasPendingIncomingRequest = if (hasPendingIncomingRequest) 1L else 0L,
            isFollower = if (isFollower) 1L else 0L,
            updatedAt = Clock.System.now().toEpochMilliseconds(),
            userId = userId
        )
    }

    suspend fun clearPendingRequest(userId: Long) = withContext(Dispatchers.Default) {
        queries.clearPendingRequest(
            updatedAt = Clock.System.now().toEpochMilliseconds(),
            userId = userId
        )
    }
}
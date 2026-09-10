package org.beem.tastymap.data.mapper

import org.beem.tastymap.data.model.profile.ProfileResponse
import org.beem.tastymap.domain.model.RelationStatus
import org.beem.tastymap.domain.model.UserProfile


fun ProfileResponse.toDomain(userId: Long): UserProfile {

    return UserProfile(
        userId = userId,
        username = username,
        name = name,
        surname = surname,
        profilePhoto = profile,
        role = role,
        biography = biography,
        postCount = postCount,
        subscriberCount = subscriberCount,
        subscribedCount = subscribedCount,
        blockedByMe = blockedByMe,
        blockedMe = blockedMe,
        relationStatus = this.relationStatus ?: RelationStatus.NOT_FOLLOWING,
        hasPendingIncomingRequest = this.hasPendingIncomingRequest,
        isFollower = this.isFollower
    )
}
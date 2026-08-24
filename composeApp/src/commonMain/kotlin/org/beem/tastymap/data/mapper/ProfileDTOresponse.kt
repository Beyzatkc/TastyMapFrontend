package org.beem.tastymap.data.mapper

import org.beem.tastymap.data.model.profile.ProfileResponse
import org.beem.tastymap.domain.model.UserProfile


fun ProfileResponse.toDomain(userId: Long): UserProfile {

    return UserProfile(
        userId = userId,
        username = username,
        name = name,
        profilePhoto = profile,
        role = role,
        biography = biography,
        postCount = postCount,
        subscriberCount = subscriberCount,
        subscribedCount = subscribedCount,
        blockedByMe = blockedByMe,
        blockedMe = blockedMe
    )
}
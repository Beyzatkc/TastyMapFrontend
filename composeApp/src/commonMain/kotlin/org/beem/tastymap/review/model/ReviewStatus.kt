package org.beem.tastymap.review.model

import kotlinx.serialization.Serializable

@Serializable
enum class ReviewStatus {
    PENDING,
    APPROVED,
    REJECTED
}
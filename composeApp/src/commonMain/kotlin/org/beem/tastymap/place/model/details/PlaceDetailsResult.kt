package org.beem.tastymap.place.model.details

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.beem.tastymap.map.model.GeometryContainer
import org.beem.tastymap.place.model.review.ReviewDto
import org.beem.tastymap.place.model.review.UserReviewSummaryDto


@Serializable
data class PlaceDetailsResult(
    @SerialName("place_id")
    val placeId: String,
    val name: String,

    val googleRating: Double? = null,
    val googleReviewCount: Int? = null,
    val tastyMapRating: Double? = null,
    val tastyMapReviewCount: Int? = null,

    val priceLevel: Int? = null,
    val types: List<String> = emptyList(),
    val formattedAddress: String? = null,
    val formattedPhoneNumber: String? = null,
    val internationalPhoneNumber: String? = null,
    val website: String? = null,


    val openingHours: OpeningHoursDto? = null,
    val geometry: GeometryContainer? = null,
    val reviews: List<ReviewDto> = emptyList(),
    val userReview: UserReviewSummaryDto? = null
)
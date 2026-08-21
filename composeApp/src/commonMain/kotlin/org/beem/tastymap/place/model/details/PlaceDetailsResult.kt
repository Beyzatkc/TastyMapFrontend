package org.beem.tastymap.place.model.details

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.beem.tastymap.place.model.review.UserReviewSummaryDto


@Serializable
data class PlaceDetailsResult(
    @SerialName("place_id")
    val placeId: String,
    val name: String,
    val rating: Double? = null,
    @SerialName("user_ratings_total")
    val userRatingsTotal: Int? = null,
    @SerialName("price_level")
    val priceLevel: Int? = null,
    val types: List<String> = emptyList(),
    @SerialName("formatted_address")
    val formattedAddress: String? = null,
    @SerialName("formatted_phone_number")
    val formattedPhoneNumber: String? = null,
    @SerialName("international_phone_number")
    val internationalPhoneNumber: String? = null,
    val website: String? = null,
    @SerialName("opening_hours")
    val openingHours: OpeningHoursDto? = null,
    @SerialName("userReview")
    val userReview: UserReviewSummaryDto? = null
)
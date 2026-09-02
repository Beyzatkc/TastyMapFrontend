package org.beem.tastymap.search.model

import org.beem.tastymap.data.model.Restaurant

fun SearchVenue.toRestaurant(): Restaurant {
    val finalRating = if ((this.tastyMapRating ?: 0.0) > 0.0) this.tastyMapRating else this.googleRating
    return Restaurant(
        id = this.placeId,
        name = this.name,
        address = this.vicinity ?: "",
        latitude = this.lat ?: 0.0,
        longitude = this.lng ?: 0.0,
        rating = finalRating,
        status = "OPERATIONAL",
        totalRatings = null,
        types = emptyList(),
        category = "restaurant"
    )
}
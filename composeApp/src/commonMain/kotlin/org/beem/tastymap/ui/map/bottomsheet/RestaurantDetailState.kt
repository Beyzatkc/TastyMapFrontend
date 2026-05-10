package org.beem.tastymap.ui.map.bottomsheet

import org.beem.tastymap.data.model.Restaurant


data class RestaurantDetailState(
    val restaurant: Restaurant? = null,
    val isLoadingPhotos: Boolean = false,
    val isFavorite: Boolean = false
)
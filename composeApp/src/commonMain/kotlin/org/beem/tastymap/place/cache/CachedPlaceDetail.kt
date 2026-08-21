package org.beem.tastymap.place.cache

import org.beem.tastymap.place.model.details.PlaceDetailsResult

data class CachedPlaceDetail(
    val data: PlaceDetailsResult,
    val cachedAt: Long
)
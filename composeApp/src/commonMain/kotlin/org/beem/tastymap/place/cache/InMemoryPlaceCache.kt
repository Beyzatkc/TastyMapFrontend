package org.beem.tastymap.place.cache

import org.beem.tastymap.place.model.details.PlaceDetailsResult
import org.beem.tastymap.place.model.review.ReviewItem
import kotlin.time.Clock

class InMemoryPlaceCache {
    private val reviewMap = mutableMapOf<String, MutableList<ReviewItem>>()
    private val endReachedPlaces = mutableSetOf<String>()

    private val detailsCache = mutableMapOf<String, CachedPlaceDetail>()
    private val DETAIL_TTL_MILLIS = 5 * 60 * 1000L


    fun getPlaceDetails(placeId: String): PlaceDetailsResult? {
        val cached = detailsCache[placeId] ?: return null
        val now = Clock.System.now().toEpochMilliseconds()

        return if (now - cached.cachedAt < DETAIL_TTL_MILLIS) {
            cached.data
        } else {
            detailsCache.remove(placeId)
            null
        }
    }

    fun putPlaceDetails(placeId: String, details: PlaceDetailsResult) {
        val now = Clock.System.now().toEpochMilliseconds()
        detailsCache[placeId] = CachedPlaceDetail(data = details, cachedAt = now)
    }

    fun getPage(placeId: String, page: Int, size: Int): List<ReviewItem>? {
        val list = reviewMap[placeId] ?: return null
        val startIndex = page * size

        if (startIndex >= list.size) return null

        val endIndex = minOf(startIndex + size, list.size)
        val sublist = list.subList(startIndex, endIndex)

        if (sublist.size < size && !endReachedPlaces.contains(placeId)) {
            return null
        }

        return sublist
    }

    fun appendReviews(placeId: String, newReviews: List<ReviewItem>, isLastPage: Boolean) {
        val currentList = reviewMap.getOrPut(placeId) { mutableListOf() }
        val existingIds = currentList.map { it.id }.toSet()
        val distinctNew = newReviews.filter { it.id !in existingIds }
        currentList.addAll(distinctNew)

        if (isLastPage) {
            endReachedPlaces.add(placeId)
        }
    }

    fun isEndReached(placeId: String): Boolean = endReachedPlaces.contains(placeId)

    fun clear(placeId: String? = null) {
        if (placeId != null) {
            reviewMap.remove(placeId)
            endReachedPlaces.remove(placeId)
            detailsCache.remove(placeId)
        } else {
            reviewMap.clear()
            endReachedPlaces.clear()
            detailsCache.clear()
        }
    }
}
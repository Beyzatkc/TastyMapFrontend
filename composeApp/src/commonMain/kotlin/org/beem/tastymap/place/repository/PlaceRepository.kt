package org.beem.tastymap.place.repository

import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.network.safeApiCall
import org.beem.tastymap.core.util.toFormatTimestamp
import org.beem.tastymap.data.model.BaseResponse
import org.beem.tastymap.place.api.PlaceDataSource
import org.beem.tastymap.place.cache.InMemoryPlaceCache
import org.beem.tastymap.place.model.details.PlaceDetailsResult
import org.beem.tastymap.place.model.review.ReviewResponse
import org.beem.tastymap.review.model.CreatedReviewRes
import org.beem.tastymap.review.model.ScoreDto
import org.beem.tastymap.review.model.ScoreType
import org.beem.tastymap.review.model.SentReviewReq
import kotlin.time.Clock

class PlaceRepository(
    private val placeDataSource: PlaceDataSource,
    private val memoryCache: InMemoryPlaceCache
) {

    suspend fun fetchPlaceDetails(
        placeId: String,
        forceRefresh: Boolean = false
    ): ResultWrapper<PlaceDetailsResult> {
        if (!forceRefresh) {
            memoryCache.getPlaceDetails(placeId)?.let { cached ->
                println("TastyMap Repo -> [$placeId] Detaylar RAM üzerinden verildi.")
                return ResultWrapper.Success(cached)
            }
        }

        println("TastyMap Repo -> [$placeId] Detaylar Ağdan isteniyor...")
        return safeApiCall {
            val response = placeDataSource.getPlaceDetails(placeId)
            val details = response.result ?: throw Exception("Mekan detay verisi boş döndü.")

            memoryCache.putPlaceDetails(placeId, details)
            details
        }
    }

    suspend fun loadReviews(placeId: String, page: Int, size: Int): ResultWrapper<BaseResponse<ReviewResponse>> {
        val cachedPage = memoryCache.getPage(placeId, page, size)
        if (cachedPage != null) {
            println("TastyMap Repo -> [$placeId] Sayfa $page RAM üzerinden verildi (${cachedPage.size} adet).")
            return ResultWrapper.Success(
                BaseResponse(
                    data = ReviewResponse(
                        reviewList = cachedPage,
                        page = page,
                        size = size,
                        placeId = placeId
                    ),
                    message = "Loaded from memory cache",
                    success = true,
                    timestamp = Clock.System.now().toEpochMilliseconds().toFormatTimestamp(),
                    errorCode = ""
                )
            )
        }

        println("TastyMap Repo -> [$placeId] Sayfa $page Ağdan isteniyor...")
        return safeApiCall {
            val response = placeDataSource.getPlaceReviews(placeId, page, size)
            val items = response.data?.reviewList.orEmpty()
            val isLast = items.isEmpty() || items.size < size

            memoryCache.appendReviews(placeId, items, isLast)
            response
        }
    }


    suspend fun submitReview(
        placeId: String,
        mainScore: Double,
        comment: String?,
        scores: Map<ScoreType, Double>,
        parentId: Long? = null
    ): ResultWrapper<BaseResponse<CreatedReviewRes>> {
        val scoreDtoList = mutableListOf<ScoreDto>()

        // 1. Ana Puanı her zaman OVERALL olarak ekle (Backend ReviewEntity rating'ini buradan hesaplar)
        scoreDtoList.add(ScoreDto(type = ScoreType.OVERALL, score = mainScore))

        // 2. Kullanıcının puanladığı (> 0.0) alt kriterleri ekle
        scores.filter { it.value > 0.0 }.forEach { (type, score) ->
            scoreDtoList.add(ScoreDto(type = type, score = score))
        }

        val request = SentReviewReq(
            parentId = parentId,
            content = comment?.takeIf { it.isNotBlank() },
            placeId = placeId,
            scores = scoreDtoList
        )

        val result = safeApiCall {
            placeDataSource.sendPlaceReview(request)
        }

        if (result is ResultWrapper.Success) {
            clearPlaceCache(placeId)
        }

        return result
    }

    fun clearPlaceCache(placeId: String? = null) {
        memoryCache.clear(placeId)
    }
}
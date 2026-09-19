package org.beem.tastymap.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.beem.tastymap.core.provider.DispatcherProvider
import org.beem.tastymap.data.model.visit.VisitResponse
import org.beem.tastymap.sqldelight.VisitEntityQueries


class VisitLocalDataSource(
    private val queries: VisitEntityQueries,
    private val dispatchers: DispatcherProvider
) {

    fun getVisitsFlow(): Flow<List<VisitResponse>> {
        return queries.getVisits()
            .asFlow()
            .mapToList(dispatchers.io)
            .map { list ->
                list.map { entity ->
                    VisitResponse(
                        visitId = entity.visitId,
                        createdAt = entity.createdAt,
                        placeId = entity.placeId,
                        placeName = entity.placeName,
                        categories = entity.categories,
                        city = entity.city,
                        district = entity.district,
                        neighbourhood = entity.neighbourhood,
                        latitude = entity.latitude,
                        longitude = entity.longitude,
                        averagePoint = entity.averagePoint
                    )
                }
            }
            .flowOn(dispatchers.io)
    }
    suspend fun deleteVisit(
        visitId: Long
    ) = withContext(dispatchers.io) {
        queries.softDeleteVisit(
            visitId = visitId
        )
    }
    suspend fun saveVisitLocal(visit: VisitResponse) = withContext(dispatchers.io) {
        queries.insertVisit(
            visitId = visit.visitId,
            placeId = visit.placeId,
            placeName = visit.placeName,
            categories = visit.categories,
            city = visit.city,
            district = visit.district,
            neighbourhood = visit.neighbourhood,
            latitude = visit.latitude,
            longitude = visit.longitude,
            averagePoint = visit.averagePoint,
            createdAt = visit.createdAt,
            isDelete = 0L
        )
        queries.trimOldVisits()
    }

}
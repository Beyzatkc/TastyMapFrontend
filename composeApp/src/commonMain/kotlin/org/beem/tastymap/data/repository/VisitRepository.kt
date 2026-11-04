package org.beem.tastymap.data.repository

import kotlinx.coroutines.flow.Flow
import org.beem.tastymap.core.local.UserManager
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.network.safeApiCall
import org.beem.tastymap.data.cache.VisitMemoryCache
import org.beem.tastymap.data.local.VisitLocalDataSource
import org.beem.tastymap.data.model.PageResponse
import org.beem.tastymap.data.model.visit.VisitRequest
import org.beem.tastymap.data.model.visit.VisitResponse
import org.beem.tastymap.data.remote.VisitDataSource

class VisitRepository(
    private val dataSource: VisitDataSource,
    private val localDataSource: VisitLocalDataSource,
    private val memoryCache: VisitMemoryCache
) {

    fun getVisitsFlow(): Flow<List<VisitResponse>> {
        return localDataSource.getVisitsFlow()
    }

    suspend fun syncVisits():ResultWrapper<PageResponse<VisitResponse>>{

        val remoteVisits = safeApiCall {
            dataSource.getVisit()
        }

        if (remoteVisits is ResultWrapper.Success) {
            remoteVisits.data.content.forEach { visit ->
                localDataSource.saveVisitLocal(visit)
            }
        }

        return remoteVisits
    }

    suspend fun addVisit(request: VisitRequest): ResultWrapper<VisitResponse> {
        val result = safeApiCall {
            dataSource.createVisitAction(request)
        }

        return when (result) {
            is ResultWrapper.Success -> {
                localDataSource.saveVisitLocal(result.data)
                memoryCache.clear()

                result
            }
            is ResultWrapper.Error -> {
                result
            }
        }
    }

    suspend fun deleteVisit(visitId: Long): ResultWrapper<Unit> {
        val result =safeApiCall {
            dataSource.deleteVisit(visitId)
        }

        if (result is ResultWrapper.Success) {
            memoryCache.clear()
            localDataSource.deleteVisit(visitId)
        }

        return result
    }

    suspend fun getPagedVisits(page: Int): ResultWrapper<PageResponse<VisitResponse>> {
        val result = safeApiCall {
            dataSource.getVisit(page = page, size = 20)
        }
        if (result is ResultWrapper.Success) {
            result.data.content.forEach { visit ->
                localDataSource.saveVisitLocal(visit)
            }
        }
        return result
    }
}
package org.beem.tastymap.data.repository

import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.network.safeApiCall
import org.beem.tastymap.data.model.health.HealthRequest
import org.beem.tastymap.data.model.health.HealthResponse
import org.beem.tastymap.data.remote.HealthDataSource

class HealthRepository(
    private val dataSource: HealthDataSource
) {
    suspend fun addHealth(request: HealthRequest): ResultWrapper<HealthResponse> {
        return safeApiCall {
            dataSource.addHealth(request)
        }
    }
    suspend fun updateHealth(request: HealthRequest): ResultWrapper<HealthResponse> {
        return safeApiCall {
            dataSource.updateHealth(request)
        }
    }
    suspend fun getHealth(): ResultWrapper<HealthResponse> {
        return safeApiCall {
            dataSource.getHealth()
        }
    }

}
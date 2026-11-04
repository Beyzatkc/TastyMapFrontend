package org.beem.tastymap.data.repository

import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.network.safeApiCall
import org.beem.tastymap.data.cache.CacheManager
import org.beem.tastymap.data.model.deleteaccount.DeleteAccountRequest
import org.beem.tastymap.data.remote.DeleteAccountDataSource
import org.beem.tastymap.domain.auth.ClearSessionUseCase

class DeleteAccountRepository(
    private val dataSource: DeleteAccountDataSource,
    private val clearSessionUseCase: ClearSessionUseCase,
    private val cacheManager: CacheManager
) {
    suspend fun deleteAccount(request: DeleteAccountRequest): ResultWrapper<Unit>{
        val result = safeApiCall { dataSource.deleteAccount(request) }

        if (result is ResultWrapper.Success) {
            clearSessionUseCase()
            cacheManager.clearAllMemoryCaches()
        }
       return result
    }
}
package org.beem.tastymap.data.repository

import kotlinx.coroutines.flow.Flow
import org.beem.tastymap.core.local.UserManager
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.network.safeApiCall
import org.beem.tastymap.data.cache.SearchMemoryCache
import org.beem.tastymap.data.local.SearchHistoryLocalDataSource
import org.beem.tastymap.data.model.PageResponse
import org.beem.tastymap.data.model.search.UserSearchResponse
import org.beem.tastymap.data.remote.SearchUserDataSource

class SearchUserRepository(
    private val remoteDataSource: SearchUserDataSource,
    private val localDataSource: SearchHistoryLocalDataSource,
    private val memoryCache: SearchMemoryCache,
    private val userManager: UserManager
) {

    fun isMe(userId: Long): Boolean {
        return userId == userManager.getUserId()
    }
    fun getRecentSearchesFlow(): Flow<List<UserSearchResponse>> {
        return localDataSource.getRecentSearchesFlow()
    }

    suspend fun getSearchUsers(
        keyword: String,
        page: Int = 0,
        size: Int = 20
    ): ResultWrapper<List<UserSearchResponse>> {
        val cachedData = memoryCache.getSearch(keyword, page)
        if (cachedData != null) {
            return ResultWrapper.Success(cachedData)
        }

        val result = safeApiCall {
            remoteDataSource.getSearchUsers(keyword, page, size)
        }

        if (result is ResultWrapper.Success) {
            memoryCache.putSearch(keyword, page, result.data)
        }

        return result
    }

    suspend fun getHistorySearchUsers(): ResultWrapper<PageResponse<UserSearchResponse>> {
        val cachedHistory = memoryCache.getHistory()
        if (cachedHistory != null) {
            return ResultWrapper.Success(cachedHistory)
        }

        val result = safeApiCall {
            remoteDataSource.getHistorySearchUsers()
        }

        return when (result) {
            is ResultWrapper.Success -> {
                localDataSource.replaceAll(result.data.content)

                memoryCache.putHistory(result.data)

                result
            }
            is ResultWrapper.Error -> {
                result
            }
        }
    }

    suspend fun addToHistory(user: UserSearchResponse): ResultWrapper<Unit> {
        localDataSource.saveSearch(user)
        memoryCache.clearHistory()

        return safeApiCall {
            remoteDataSource.addToHistory(user.id)
        }
    }

    suspend fun deleteFromHistory(userIdToDelete: Long): ResultWrapper<Unit> {
        localDataSource.deleteSearch(userIdToDelete)
        memoryCache.clearHistory()

        return safeApiCall {
            remoteDataSource.deleteFromHistory(userIdToDelete)
        }
    }
}
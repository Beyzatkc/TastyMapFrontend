package org.beem.tastymap.domain.auth

import org.beem.tastymap.core.local.TokenManager
import org.beem.tastymap.core.local.UserManager
import org.beem.tastymap.data.cache.CacheManager
import org.beem.tastymap.data.cache.ProfileMemoryCache
import org.beem.tastymap.data.local.ProfileLocalDataSource

class ClearSessionUseCase(
    private val tokenManager: TokenManager,
    private val userManager: UserManager,
    private val cacheManager: CacheManager,
    private val localDataSource: ProfileLocalDataSource
) {
    suspend operator fun invoke() {
        tokenManager.clear()
        userManager.clear()
        cacheManager.clearAllMemoryCaches()
        localDataSource.clearAll()
    }
}
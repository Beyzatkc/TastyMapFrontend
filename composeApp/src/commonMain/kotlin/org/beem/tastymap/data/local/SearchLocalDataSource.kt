package org.beem.tastymap.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.beem.tastymap.core.provider.DispatcherProvider
import org.beem.tastymap.data.model.search.UserSearchResponse
import org.beem.tastymap.sqldelight.SearchHistoryEntityQueries
import kotlin.time.Clock

class SearchHistoryLocalDataSource(
    private val queries: SearchHistoryEntityQueries,
    private val dispatchers: DispatcherProvider
) {

    fun getRecentSearchesFlow(): Flow<List<UserSearchResponse>> {
        return queries.getAllRecentSearches()
            .asFlow()
            .mapToList(dispatchers.io)
            .map { entities ->
                entities.map { entity ->
                    UserSearchResponse(
                        id = entity.id,
                        username = entity.username,
                        name = entity.name,
                        profile = entity.profile
                    )
                }
            }
    }

    suspend fun getRecentSearchesOnce(): List<UserSearchResponse> = withContext(dispatchers.io) {
        queries.getAllRecentSearches().executeAsList().map { entity ->
            UserSearchResponse(
                id = entity.id,
                username = entity.username,
                name = entity.name,
                profile = entity.profile
            )
        }
    }

    suspend fun replaceAll(users: List<UserSearchResponse>) = withContext(dispatchers.io) {
        queries.transaction {
            queries.clearAll()
            val now = Clock.System.now().toEpochMilliseconds()
            users.forEach { user ->
                queries.insertOrReplace(
                    id = user.id,
                    username = user.username,
                    name = user.name,
                    profile = user.profile,
                    searchedAt = now
                )
            }
        }
    }

    suspend fun saveSearch(user: UserSearchResponse) = withContext(dispatchers.io) {
        queries.insertOrReplace(
            id = user.id,
            username = user.username,
            name = user.name,
            profile = user.profile,
            searchedAt = Clock.System.now().toEpochMilliseconds()
        )
    }

    suspend fun deleteSearch(userId: Long) = withContext(dispatchers.io) {
        queries.deleteById(userId)
    }

    suspend fun clearAll() = withContext(dispatchers.io) {
        queries.clearAll()
    }
}
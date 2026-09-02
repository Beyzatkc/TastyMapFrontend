package org.beem.tastymap.search.repository

import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.network.safeApiCall
import org.beem.tastymap.search.model.SearchVenue
import org.beem.tastymap.search.network.SearchDataSource

class SearchRepository(
    private val dataSource: SearchDataSource
) {
    suspend fun searchVenues(query: String): ResultWrapper<List<SearchVenue>> {
        if (query.isBlank() || query.length < 2) {
            return ResultWrapper.Success(emptyList())
        }
        return safeApiCall {
            val response = dataSource.searchVenues(searchText = query)
            response.data.venues
        }
    }
}
package org.beem.tastymap.data.repository

import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.network.safeApiCall
import org.beem.tastymap.data.model.PostResponseDTO
import org.beem.tastymap.data.model.RegisterRequest
import org.beem.tastymap.data.model.RegisterResponse
import org.beem.tastymap.data.remote.AuthDataSource
import org.beem.tastymap.data.remote.UserDataSource

class PostRepository(
    private val dataSource: UserDataSource,
) {
    suspend fun getPosts(): ResultWrapper<PostResponseDTO> {
        return safeApiCall {
            dataSource.getMyPosts()
        }
    }
}
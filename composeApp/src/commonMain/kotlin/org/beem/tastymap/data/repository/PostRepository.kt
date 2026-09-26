package org.beem.tastymap.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import org.beem.tastymap.core.local.UserManager
import org.beem.tastymap.core.network.ErrorType
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.network.safeApiCall
import org.beem.tastymap.core.provider.DispatcherProvider
import org.beem.tastymap.data.local.PostLocalDataSource
import org.beem.tastymap.data.local.ProfileLocalDataSource
import org.beem.tastymap.data.model.PageResponse
import org.beem.tastymap.data.model.post.PostAndVisitRequest
import org.beem.tastymap.data.model.post.PostGridResponse
import org.beem.tastymap.data.model.post.PostLikeResponse
import org.beem.tastymap.data.model.post.PostLikeUserResponse
import org.beem.tastymap.data.model.post.PostResponse
import org.beem.tastymap.data.model.post.PostUpdateRequest
import org.beem.tastymap.data.remote.FileRemoteDataSource
import org.beem.tastymap.data.remote.PostDataSource

class PostRepository(
    private val remoteDataSource: PostDataSource,
    private val profileLocalDataSource: ProfileLocalDataSource,
    private val postLocalDataSource: PostLocalDataSource,
    private val dispatchers: DispatcherProvider,
    private val fileRemoteDataSource: FileRemoteDataSource,
    private val userManager: UserManager
) {
    val myId: Long
        get() = userManager.userSession.value?.userId ?: 0L


    fun isMe(userId: Long): Boolean {
        return userId == myId
    }
    fun getMyPostsStream(
        page: Int = 0,
        size: Int = 15
    ): Flow<List<PostGridResponse>> = getUserPostsStream(myId, page, size)


    suspend fun refreshPostDetail(postId: Long): ResultWrapper<PostResponse> = withContext(dispatchers.io) {
        fetchAndSavePostDetail(postId)
    }
    fun getUserPostsStream(
        userId: Long,
        page: Int = 0,
        size: Int = 15
    ): Flow<List<PostGridResponse>> {
        return postLocalDataSource.getUserGridPostsFlow(userId)
            .onStart {
                fetchAndSaveUserPosts(userId, page, size)
            }
            .flowOn(dispatchers.io)
    }

    fun getPostDetailStream(postId: Long): Flow<PostResponse?> {
        return postLocalDataSource.getPostDetailFlow(postId)
            .flowOn(dispatchers.io)
    }

    // Hata alınsa da arka planda sessizce kalabilir, aksi halde ResultWrapper dönerek ViewModel'e bildirilebilir
    suspend fun fetchAndSaveUserPosts(
        userId: Long,
        page: Int,
        size: Int
    ): ResultWrapper<Unit> {
        val result = if (userId == myId) {
            safeApiCall { remoteDataSource.getMyPosts(page, size) }
        } else {
            safeApiCall { remoteDataSource.getUserPosts(userId, page, size) }
        }

        return when (result) {
            is ResultWrapper.Success -> {
                postLocalDataSource.saveGridPosts(userId, result.data.content, page)
                ResultWrapper.Success(Unit)
            }
            is ResultWrapper.Error -> ResultWrapper.Error(result.message, ErrorType.UNKNOWN_ERROR)
        }
    }

    suspend fun fetchAndSavePostDetail(postId: Long): ResultWrapper<PostResponse> {
        val result = safeApiCall { remoteDataSource.getPostDetail(postId) }

        if (result is ResultWrapper.Success) {
            postLocalDataSource.savePostDetail(result.data)
        }
        if(result is ResultWrapper.Error){
            ResultWrapper.Error(result.message, ErrorType.UNKNOWN_ERROR)
        }

        return result
    }

    suspend fun fetchUserPostsPage(
        userId: Long,
        page: Int,
        size: Int = 15
    ): ResultWrapper<PageResponse<PostGridResponse>> {
        val result = if (userId == myId) {
            safeApiCall { remoteDataSource.getMyPosts(page, size) }
        } else {
            safeApiCall { remoteDataSource.getUserPosts(userId, page, size) }
        }

        if (result is ResultWrapper.Success) {
            postLocalDataSource.saveGridPosts(userId, result.data.content, page)
        }
        return result
    }

    suspend fun addPost(
        request: PostAndVisitRequest
    ): ResultWrapper<PostResponse> = withContext(dispatchers.io) {
        val result = safeApiCall { remoteDataSource.addPost(request) }
        if (result is ResultWrapper.Success) {
            postLocalDataSource.savePostDetail(result.data)
            profileLocalDataSource.incrementPostCount(myId)
        }
        result
    }

    suspend fun toggleLike(postId: Long): ResultWrapper<PostLikeResponse> = withContext(dispatchers.io) {
        postLocalDataSource.toggleLikeLocal(postId)

        val result = safeApiCall { remoteDataSource.toggleLike(postId) }

        if (result is ResultWrapper.Error) {
            postLocalDataSource.toggleLikeLocal(postId)
        }

        result
    }

    suspend fun togglePin(postId: Long): ResultWrapper<Unit> = withContext(dispatchers.io) {
        postLocalDataSource.togglePinLocal(postId)

        val result = safeApiCall { remoteDataSource.togglePin(postId) }

        if (result is ResultWrapper.Error) {
            // Hata durumunda yerel durum eski haline döndürülür
            postLocalDataSource.togglePinLocal(postId)
            return@withContext ResultWrapper.Error(result.message, ErrorType.UNKNOWN_ERROR)
        }

        ResultWrapper.Success(Unit)
    }

    suspend fun deletePost(postId: Long): ResultWrapper<Unit> = withContext(dispatchers.io) {
        val result = safeApiCall { remoteDataSource.deletePost(postId) }

        when (result) {
            is ResultWrapper.Success -> {
                postLocalDataSource.deletePostLocal(postId)
                profileLocalDataSource.decrementPostCount(myId)
                ResultWrapper.Success(Unit)
            }
            is ResultWrapper.Error -> ResultWrapper.Error(result.message, ErrorType.UNKNOWN_ERROR)
        }
    }

    suspend fun updatePost(
        postId: Long,
        request: PostUpdateRequest
    ): ResultWrapper<PostResponse> = withContext(dispatchers.io) {
        val result = safeApiCall { remoteDataSource.updatePost(postId, request) }

        if (result is ResultWrapper.Success) {
            postLocalDataSource.savePostDetail(result.data)
        }

        result
    }

    suspend fun getPostLikes(
        postId: Long,
        page: Int = 0,
        size: Int = 20
    ): ResultWrapper<PageResponse<PostLikeUserResponse>> {
        return safeApiCall { remoteDataSource.getPostLikes(postId, page, size) }
    }

    suspend fun uploadPostPhotos(imagesBytes: List<ByteArray>): ResultWrapper<List<String>> = withContext(dispatchers.io) {
        safeApiCall {
            val response = fileRemoteDataSource.uploadMultipleFiles(
                imagesBytes = imagesBytes,
                type = "posts"
            )
            response.imageUrls
        }
    }
}
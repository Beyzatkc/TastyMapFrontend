package org.beem.tastymap.data.repository

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
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
import org.beem.tastymap.data.remote.PostDataSource

class PostRepository(
    private val remoteDataSource: PostDataSource,
    private val profileLocalDataSource: ProfileLocalDataSource,
    private val postLocalDataSource: PostLocalDataSource,
    private val dispatchers: DispatcherProvider,
    private val userManager: UserManager
) {
    val myId: Long
        get() = userManager.userSession.value?.userId ?: 0L


    fun getMyPostsStream(
        page: Int = 0,
        size: Int = 15
    ): Flow<List<PostGridResponse>> = getUserPostsStream(myId, page, size)

    fun getUserPostsStream(
        userId: Long,
        page: Int = 0,
        size: Int = 15
    ): Flow<List<PostGridResponse>> = flow {
        coroutineScope {
            launch { fetchAndSaveUserPosts(userId, page, size) }

            emitAll(postLocalDataSource.getUserGridPostsFlow(userId))
        }
    }.flowOn(dispatchers.io)

    fun getPostDetailStream(postId: Long): Flow<PostResponse?> = flow {
        coroutineScope {
            launch { fetchAndSavePostDetail(postId) }
            emitAll(postLocalDataSource.getPostDetailFlow(postId))
        }
    }.flowOn(dispatchers.io)



    private suspend fun fetchAndSaveUserPosts(
        userId: Long,
        page: Int,
        size: Int
    ) {
        println(
            "DEBUG_POST: [fetchAndSaveUserPosts] START " +
                    "userId=$userId page=$page size=$size myId=$myId"
        )

        val result = if (userId == myId) {
            println("DEBUG_POST: [fetchAndSaveUserPosts] getMyPosts çağrılıyor")

            safeApiCall {
                remoteDataSource.getMyPosts(page, size)
            }
        } else {
            println(
                "DEBUG_POST: [fetchAndSaveUserPosts] " +
                        "getUserPosts çağrılıyor userId=$userId"
            )

            safeApiCall {
                remoteDataSource.getUserPosts(userId, page, size)
            }
        }

        when (result) {

            is ResultWrapper.Success -> {

                println(
                    "DEBUG_POST: [fetchAndSaveUserPosts] API SUCCESS " +
                            "contentSize=${result.data.content.size}"
                )

                println(
                    "DEBUG_POST: [fetchAndSaveUserPosts] " +
                            "page=${result.data.number} " +
                            "totalElements=${result.data.totalElements} " +
                            "totalPages=${result.data.totalPages} " +
                            "first=${result.data.first} " +
                            "last=${result.data.last}"
                )

                result.data.content.forEach {
                    println(
                        "DEBUG_POST: API POST -> " +
                                "id=${it.postId}, " +
                                "photo=${it.photoUrl}, " +
                                "pinned=${it.isPinned}"
                    )
                }

                println(
                    "DEBUG_POST: [fetchAndSaveUserPosts] " +
                            "SQLite save başlıyor..."
                )

                postLocalDataSource.saveGridPosts(
                    userId = userId,
                    posts = result.data.content,
                    page = page
                )

                println(
                    "DEBUG_POST: [fetchAndSaveUserPosts] " +
                            "SQLite save BİTTİ"
                )
            }

            is ResultWrapper.Error -> {
                println(
                    "DEBUG_POST: [fetchAndSaveUserPosts] API ERROR " +
                            "message=${result.message}"
                )
            }
        }
    }

    suspend fun fetchUserPostsPage(userId: Long, page: Int, size: Int = 15): ResultWrapper<PageResponse<PostGridResponse>> {
        println("DEBUG_POST: ID KARIS"+userId+"   "+myId)
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

    private suspend fun fetchAndSavePostDetail(postId: Long) {
        val result = safeApiCall { remoteDataSource.getPostDetail(postId) }
        if (result is ResultWrapper.Success) {
            postLocalDataSource.savePostDetail(result.data)
        }
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
            return@withContext ResultWrapper.Error(result.message, ErrorType.UNKNOWN_ERROR)
        }

        result
    }

    /**
     * Optimistic Pin Toggle
     */
    suspend fun togglePin(postId: Long): ResultWrapper<Unit> = withContext(dispatchers.io) {
        postLocalDataSource.togglePinLocal(postId)

        val result = safeApiCall { remoteDataSource.togglePin(postId) }

        if (result is ResultWrapper.Error) {
            postLocalDataSource.togglePinLocal(postId) // Rollback
            return@withContext ResultWrapper.Error(result.message, ErrorType.UNKNOWN_ERROR)
        }

        ResultWrapper.Success(Unit)
    }

    /**
     * Post Silme
     */
    suspend fun deletePost(postId: Long): ResultWrapper<Unit> = withContext(dispatchers.io) {
        val result = safeApiCall { remoteDataSource.deletePost(postId) }

        when (result) {
            is ResultWrapper.Success -> {
                postLocalDataSource.deletePostLocal(postId)
                profileLocalDataSource.decrementPostCount(myId)
                ResultWrapper.Success(Unit)
            }
            is ResultWrapper.Error -> {
                ResultWrapper.Error(result.message, ErrorType.UNKNOWN_ERROR)
            }
        }
    }

    /**
     * Post Güncelleme
     */
    suspend fun updatePost(postId: Long, request: PostUpdateRequest): ResultWrapper<PostResponse> = withContext(dispatchers.io) {
        val result = safeApiCall { remoteDataSource.updatePost(postId, request) }

        if (result is ResultWrapper.Success) {
            postLocalDataSource.savePostDetail(result.data)
        }

        result
    }


    suspend fun getWhosLike(postId: Long, page: Int = 0, size: Int = 20): ResultWrapper<PageResponse<PostLikeUserResponse>> {
        return safeApiCall { remoteDataSource.getWhosLike(postId, page, size) }
    }
}
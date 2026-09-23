package org.beem.tastymap.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import org.beem.tastymap.core.provider.DispatcherProvider
import org.beem.tastymap.data.model.post.PostGridResponse
import org.beem.tastymap.data.model.post.PostResponse
import org.beem.tastymap.sqldelight.PostEntityQueries
import kotlin.time.Clock

class PostLocalDataSource(
    private val queries: PostEntityQueries,
    private val dispatchers: DispatcherProvider
) {

    // Helper: String -> List<String>
    private fun String?.toUrlList(): List<String> {
        return if (this.isNullOrBlank()) emptyList() else this.split(",")
    }

    // Helper: List<String> -> String
    private fun List<String>?.toDbString(): String {
        return this?.joinToString(",") ?: ""
    }

    fun getUserGridPostsFlow(userId: Long): Flow<List<PostGridResponse>> {
        println("DEBUG_LOCAL: [getUserGridPostsFlow] Dinlenmeye başlandı. Hedef userId = $userId")

        return queries.getUserGridPosts(userId)
            .asFlow()
            .mapToList(dispatchers.io)
            .onEach { rawList ->
                println("DEBUG_LOCAL: [getUserGridPostsFlow] SQL'den HAM liste boyutu: ${rawList.size} (userId = $userId)")
            }
            .map { list ->
                list.map { entity ->
                    val photoUrlsList = entity.photoUrls.toUrlList()
                    PostGridResponse(
                        postId = entity.postId,
                        photoUrl = photoUrlsList.firstOrNull().orEmpty(), // Grid kapak fotoğrafı
                        isPinned = entity.isPinned == 1L
                    )
                }
            }
            .onEach { mappedList ->
                println("DEBUG_LOCAL: [getUserGridPostsFlow] UI'a iletilen Mapped liste boyutu: ${mappedList.size}")
            }
            .flowOn(dispatchers.io)
    }

    fun getPostDetailFlow(postId: Long): Flow<PostResponse?> {
        return queries.getPostDetailById(postId)
            .asFlow()
            .mapToOneOrNull(dispatchers.io)
            .map { entity ->
                entity?.let {
                    PostResponse(
                        postId = it.postId,
                        userId = it.userId,
                        photoUrls = it.photoUrls.toUrlList(),
                        explanation = it.explanation,
                        point = 0,
                        numberOfLikes = it.numberOfLikes.toInt(),
                        commentCount = it.commentCount.toInt(),
                        isLiked = it.isLiked == 1L,
                        isPinned = it.isPinned == 1L,
                        isCommentEnabled = it.commentEnabled == 1L,
                        createdAt = it.createdAt.toString(),
                        username = it.username.orEmpty(),
                        profilePhotoUrl = it.profilePhotoUrl,
                        placeId = it.placeId,
                        placeName = it.placeName,
                        categories = it.categories,
                        city = it.city,
                        district = it.district,
                        neighbourhood = it.neighbourhood,
                        latitude = it.latitude ?: 0.0,
                        longitude = it.longitude ?: 0.0,
                        averagePoint = it.averagePoint ?: 0.0
                    )
                }
            }
            .flowOn(dispatchers.io)
    }

    suspend fun saveGridPosts(
        userId: Long,
        posts: List<PostGridResponse>,
        page: Int
    ) = withContext(dispatchers.io) {

        println("DEBUG_SQL: SAVE START userId=$userId page=$page count=${posts.size}")
        val now = Clock.System.now().toEpochMilliseconds()

        try {
            queries.transaction {
                posts.forEach { post ->
                    val exists = queries.checkPostExists(post.postId).executeAsOne() > 0

                    if (exists) {
                        println("DEBUG_SQL: GÜNCELLENİYOR -> postId=${post.postId}")
                        queries.updateGridPost(
                            postId = post.postId,
                            userId = userId,
                            photoUrls = post.photoUrl, // Grid'den gelen kapak fotoğrafı
                            isPinned = if (post.isPinned) 1L else 0L,
                            page = page.toLong(),
                            updatedAt = now
                        )
                    } else {
                        println("DEBUG_SQL: YENİ EKLENİYOR -> postId=${post.postId}")
                        queries.insertGridPost(
                            postId = post.postId,
                            userId = userId,
                            photoUrls = post.photoUrl,
                            isPinned = if (post.isPinned) 1L else 0L,
                            page = page.toLong(),
                            updatedAt = now
                        )
                    }
                }
            }
            println("DEBUG_SQL: Transaction başarılı.")
        } catch (e: Exception) {
            println("DEBUG_SQL: HATA PATLADI -> ${e.message}")
            e.printStackTrace()
        }

        val savedPosts = queries.getUserGridPosts(userId).executeAsList()
        println("DEBUG_SQL: TRANSACTION SONRASI DB count=${savedPosts.size}")
    }

    suspend fun savePostDetail(post: PostResponse) = withContext(dispatchers.io) {
        val now = Clock.System.now().toEpochMilliseconds()

        queries.transaction {
            val exists = queries.checkPostExists(post.postId).executeAsOne() > 0

            if (exists) {
                queries.updatePostDetail(
                    postId = post.postId,
                    photoUrls = post.photoUrls.toDbString(), // List -> String dönüştürüldü
                    explanation = post.explanation,
                    numberOfLikes = post.numberOfLikes.toLong(),
                    commentCount = post.commentCount.toLong(),
                    isLiked = if (post.isLiked) 1L else 0L,
                    commentEnabled = if (post.isCommentEnabled) 1L else 0L,
                    updateDate = post.updateDate?.toLongOrNull(),
                    username = post.username,
                    profilePhotoUrl = post.profilePhotoUrl,
                    placeId = post.placeId,
                    placeName = post.placeName,
                    categories = post.categories,
                    city = post.city,
                    district = post.district,
                    neighbourhood = post.neighbourhood,
                    latitude = post.latitude,
                    longitude = post.longitude,
                    averagePoint = post.averagePoint,
                    updatedAt = now
                )
            } else {
                queries.insertPostDetail(
                    postId = post.postId,
                    userId = post.userId,
                    photoUrls = post.photoUrls.toDbString(),
                    isPinned = if (post.isPinned) 1L else 0L,
                    explanation = post.explanation,
                    numberOfLikes = post.numberOfLikes.toLong(),
                    commentCount = post.commentCount.toLong(),
                    isLiked = if (post.isLiked) 1L else 0L,
                    commentEnabled = if (post.isCommentEnabled) 1L else 0L,
                    createdAt = post.createdAt.toLongOrNull(),
                    updateDate = post.updateDate?.toLongOrNull(),
                    username = post.username,
                    profilePhotoUrl = post.profilePhotoUrl,
                    placeId = post.placeId,
                    placeName = post.placeName,
                    categories = post.categories,
                    city = post.city,
                    district = post.district,
                    neighbourhood = post.neighbourhood,
                    latitude = post.latitude,
                    longitude = post.longitude,
                    averagePoint = post.averagePoint,
                    page = 0L,
                    updatedAt = now
                )
            }
            queries.trimOldPosts()
        }
    }

    suspend fun toggleLikeLocal(postId: Long) = withContext(dispatchers.io) {
        queries.toggleLikeLocal(
            updatedAt = Clock.System.now().toEpochMilliseconds(),
            postId = postId
        )
    }

    suspend fun togglePinLocal(postId: Long) = withContext(dispatchers.io) {
        queries.togglePinLocal(
            updatedAt = Clock.System.now().toEpochMilliseconds(),
            postId = postId
        )
    }

    suspend fun deletePostLocal(postId: Long) = withContext(dispatchers.io) {
        queries.deletePostById(postId)
    }

    suspend fun clearAll() = withContext(dispatchers.io) {
        queries.clearAllPosts()
    }
}
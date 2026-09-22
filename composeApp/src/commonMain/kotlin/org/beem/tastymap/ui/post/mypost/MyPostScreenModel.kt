package org.beem.tastymap.ui.post.mypost

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.data.model.post.PostUpdateRequest
import org.beem.tastymap.data.repository.PostRepository
import org.beem.tastymap.ui.post.otherpost.PostListUiState

class MyPostScreenModel(
    private val postRepository: PostRepository,
) : ScreenModel {
    private val pageSize = 12
    private val _uiState = MutableStateFlow(PostListUiState())
    val uiState = _uiState.asStateFlow()
    private var observeJob: Job? = null

    fun loadInitialData() {
        val currentMyId = postRepository.myId
        println("DEBUG_POST: [loadInitialData] Tetiklendi. myId = $currentMyId")

        if (observeJob != null) {
            println("DEBUG_POST: [loadInitialData] observeJob zaten aktif, tekrar başlatılmadı.")
            return
        }

        _uiState.update { it.copy(isLoading = true) }

        observeJob = screenModelScope.launch {
            postRepository.getMyPostsStream(page = 0, size = pageSize)
                .onStart {
                    println("DEBUG_POST: [getMyPostsStream] Flow dinlenmeye başlandı.")
                }
                .catch { e ->
                    println("DEBUG_POST: [getMyPostsStream] HATA ALINDI! Message: ${e.message}")
                    e.printStackTrace()
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
                .collect { posts ->
                    println("DEBUG_POST: [getMyPostsStream] Veri geldi! Eleman sayısı: ${posts.size}")
                    posts.forEachIndexed { index, post ->
                        println("DEBUG_POST:   -> Post[$index]: ID=${post.postId}, Photo=${post.photoUrl}, Pinned=${post.isPinned}")
                    }

                    _uiState.update { currentState ->
                        currentState.copy(
                            items = posts,
                            isLoading = false,
                            isRefreshing = false,
                            isLoadingMore = false
                        )
                    }
                }
        }
    }

    fun loadNextPage() {
        val currentState = _uiState.value
        println("DEBUG_POST: [loadNextPage] İsteniyor. CurrentPage: ${currentState.currentPage}, isLoading: ${currentState.isLoading}")

        if (currentState.isLoading || currentState.isLoadingMore || currentState.isLastPage) return

        _uiState.update { it.copy(isLoadingMore = true) }

        screenModelScope.launch {
            val nextPage = currentState.currentPage + 1
            val result = postRepository.fetchUserPostsPage(
                userId = postRepository.myId,
                page = nextPage,
                size = pageSize
            )

            when (result) {
                is ResultWrapper.Success -> {
                    val isLast = result.data.last ?: (result.data.content.size < pageSize)
                    println("DEBUG_POST: [loadNextPage] Başarılı! Yeni gelen post sayısı: ${result.data.content.size}")
                    _uiState.update {
                        it.copy(
                            currentPage = nextPage,
                            isLastPage = isLast,
                            isLoadingMore = false
                        )
                    }
                }
                is ResultWrapper.Error -> {
                    println("DEBUG_POST: [loadNextPage] Hata: ${result.message}")
                    _uiState.update {
                        it.copy(isLoadingMore = false, errorMessage = result.message)
                    }
                }
            }
        }
    }

    fun refresh() {
        println("DEBUG_POST: [refresh] Yenileme başlatıldı.")
        if (_uiState.value.isRefreshing) return

        _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }

        screenModelScope.launch {
            val result = postRepository.fetchUserPostsPage(
                userId = postRepository.myId,
                page = 0,
                size = pageSize
            )

            if (result is ResultWrapper.Error) {
                println("DEBUG_POST: [refresh] Hata alındı: ${result.message}")
                _uiState.update { it.copy(isRefreshing = false, errorMessage = result.message) }
            } else {
                println("DEBUG_POST: [refresh] Başarıyla yenilendi.")
                _uiState.update { it.copy(currentPage = 0, isRefreshing = false) }
            }
        }
    }



    fun deletePost(postId: Long) {
        screenModelScope.launch {
            val result = postRepository.deletePost(postId)
            if (result is ResultWrapper.Error) {
                _uiState.update { it.copy(errorMessage = result.message) }
            }
        }
    }

    //UPDATEYE BAKICLAK YANLIS SUAN BURDA OLCAYACAK
    fun updatePost(postId: Long, request: PostUpdateRequest) {
        screenModelScope.launch {
            val result = postRepository.updatePost(postId, request)
            if (result is ResultWrapper.Error) {
                _uiState.update { it.copy(errorMessage = result.message) }
            }
            // Başarılı olunca SQLite güncellenir ve Flow ekranı otomatik yeniler.
        }
    }

    fun togglePin(postId: Long) {
        screenModelScope.launch {
            val result = postRepository.togglePin(postId)
            if (result is ResultWrapper.Error) {
                _uiState.update { it.copy(errorMessage = result.message) }
            }
        }
    }
}
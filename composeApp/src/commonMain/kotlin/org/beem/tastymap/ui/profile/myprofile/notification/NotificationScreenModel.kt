package org.beem.tastymap.ui.profile.myprofile.notification

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.data.model.socialnotifications.NotificationActionStatus
import org.beem.tastymap.data.repository.SocialNotificationsRepository
import org.beem.tastymap.domain.model.RelationStatus
import org.beem.tastymap.domain.usecase.ToggleFollowUseCase
import org.beem.tastymap.ui.common.NotificationBadgeManager


class NotificationScreenModel(
    private val notificationRepository: SocialNotificationsRepository,
    private val toggleFollowUseCase: ToggleFollowUseCase,
    private val badgeManager: NotificationBadgeManager
): ScreenModel {
    private val _uiState = MutableStateFlow(SocialNotificationsState())
    val uiState = _uiState.asStateFlow()

    private val pageSize = 10

    fun loadInitialData() {
        // 1. Yeni bildirim gelmiş olabileceği için önbelleği temizle
        notificationRepository.clearCache()

        // 2. Yüklenme durumunu başlat ve ilk sayfayı doğrudan ağdan çek (forceFetch = true)
        _uiState.update {
            it.copy(
                isLoading = true,
                errorMessage = null
            )
        }

        fetchPage(page = 0, isRefresh = true)
    }


    fun loadNextPage() {
        val currentState = _uiState.value
        if (currentState.isLoading || currentState.isLoadingMore || currentState.isLastPage) return

        _uiState.update { it.copy(isLoadingMore = true) }
        fetchPage(page = currentState.currentPage + 1, isRefresh = false)
    }
    fun refresh() {
        if (_uiState.value.isRefreshing) return

        _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
        fetchPage(page = 0, isRefresh = true)
    }

    private fun fetchPage(page: Int, isRefresh: Boolean) {
        screenModelScope.launch {
            val result = notificationRepository.getNotifications(page,pageSize,isRefresh)

            when (result) {
                is ResultWrapper.Success -> {
                    val pageData = result.data
                    val newItems = pageData.content
                    val isLast = pageData.last ?: (newItems.size < pageSize)

                    _uiState.update { currentState ->
                        val updatedList = if (isRefresh || page == 0) {
                            newItems
                        } else {
                            currentState.items + newItems
                        }

                        currentState.copy(
                            items = updatedList,
                            isLoading = false,
                            isRefreshing = false,
                            isLoadingMore = false,
                            currentPage = page,
                            isLastPage = isLast,
                            errorMessage = null
                        )
                    }
                }
                is ResultWrapper.Error -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            isRefreshing = false,
                            isLoadingMore = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    fun markAllAsRead() {
        badgeManager.updateBadge(false)

        screenModelScope.launch {
            notificationRepository.markAsRead()
        }
    }


    fun acceptRequest(notificationId: Long, requesterId: Long) {
        executeFollowAction(
            notificationId = notificationId,
            targetUserId = requesterId,
            action = ToggleFollowUseCase.Action.AcceptRequest,
            newActionStatus = NotificationActionStatus.ACCEPTED // İşlem bitince durum ACCEPTED olacak
        )
    }

    fun rejectRequest(notificationId: Long, requesterId: Long) {
        executeFollowAction(
            notificationId = notificationId,
            targetUserId = requesterId,
            action = ToggleFollowUseCase.Action.RejectRequest,
            newActionStatus = NotificationActionStatus.REJECTED // İşlem bitince durum REJECTED olacak
        )
    }

    // 3. Geri Takip Et / Takipten Çık (Toggle)
    fun toggleFollow(notificationId: Long, targetUserId: Long, currentStatus: RelationStatus) {
        executeFollowAction(
            notificationId = notificationId,
            targetUserId = targetUserId,
            action = ToggleFollowUseCase.Action.ToggleFollow(currentStatus),
            newActionStatus = null // actionStatus değişmeyecek (zaten ACCEPTED durumunda kalacak), sadece relationStatus değişecek
        )
    }

    // --- ORTAK İŞLEM YÜRÜTÜCÜ ---
    private fun executeFollowAction(
        notificationId: Long,
        targetUserId: Long,
        action: ToggleFollowUseCase.Action,
        newActionStatus: NotificationActionStatus?
    ) {
        screenModelScope.launch {
            when (val result = toggleFollowUseCase(targetUserId, action)) {
                is ResultWrapper.Success -> {
                    val actionResult = result.data

                    _uiState.update { currentState ->
                        val updatedItems = currentState.items.map { notification ->
                            if (notification.id == notificationId) {
                                val updatedActor = notification.actor.copy(
                                    relationStatus = actionResult.relationStatus
                                )

                                val finalActionStatus = newActionStatus ?: notification.actionStatus

                                notification.copy(
                                    actor = updatedActor,
                                    actionStatus = finalActionStatus
                                )
                            } else {
                                notification
                            }
                        }

                        currentState.copy(
                            items = updatedItems,
                            errorMessage = null
                        )
                    }
                }
                is ResultWrapper.Error -> {
                    _uiState.update { it.copy(errorMessage = result.message) }
                }
            }
        }
    }

}
package org.beem.tastymap.ui.profile.otherprofile

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.beem.tastymap.core.local.UserManager
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.data.repository.SubscribersRepository
import org.beem.tastymap.data.repository.profile.ProfileRepository
import org.beem.tastymap.domain.model.RelationStatus
import org.beem.tastymap.domain.model.UserProfile
import org.beem.tastymap.domain.usecase.ToggleFollowUseCase
import org.beem.tastymap.ui.profile.otherprofile.ProfileUiState


class ProfileScreenModel(
    private val repo: ProfileRepository,
    private val toggleFollowUseCase: ToggleFollowUseCase,
): ScreenModel {

    private val _profileState = MutableStateFlow(ProfileUiState())
    val profileState = _profileState.asStateFlow()

    private var profileJob: Job? = null


    fun getProfile(userId: Long ,isFromPullToRefresh: Boolean = false) {
        // Eğer pull-to-refresh değilse ve zaten aktif bir dinleme varsa tekrar başlatma
        if (!isFromPullToRefresh && profileJob?.isActive == true) return

        // Pull-to-refresh yapılıyorsa eski job'ı iptal et
        profileJob?.cancel()

        profileJob = screenModelScope.launch {
            _profileState.update {
                if (isFromPullToRefresh) {
                    it.copy(isRefreshing = true, errorMessage = null)
                } else {
                    it.copy(isLoading = it.profile == null, errorMessage = null)
                }
            }

            repo.getMyProfile(userId).collect { result ->
                when (result) {
                    is ResultWrapper.Success -> {
                        _profileState.update {
                            it.copy(
                                isLoading = false,
                                isRefreshing = false,
                                profile = result.data,
                                errorMessage = null
                            )
                        }
                    }
                    is ResultWrapper.Error -> {
                        _profileState.update {
                            it.copy(
                                isLoading = false,
                                isRefreshing = false,
                                errorMessage = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun handleFollowAction(targetUserId: Long, currentStatus: RelationStatus) {
        executeAction(
            targetUserId = targetUserId,
            action = ToggleFollowUseCase.Action.ToggleFollow(currentStatus)
        )
    }

    fun acceptRequest(requesterId: Long) {
        executeAction(
            targetUserId = requesterId,
            action = ToggleFollowUseCase.Action.AcceptRequest
        )
    }

    fun rejectRequest(requesterId: Long) {
        executeAction(
            targetUserId = requesterId,
            action = ToggleFollowUseCase.Action.RejectRequest
        )
    }

    private fun executeAction(targetUserId: Long, action: ToggleFollowUseCase.Action) {
        screenModelScope.launch {
            _profileState.update { it.copy(isActionLoading = true, errorMessage = null) }

            when (val result = toggleFollowUseCase(targetUserId, action)) {
                is ResultWrapper.Success -> {
                    val actionResult = result.data

                    _profileState.update { currentState ->
                        currentState.copy(
                            isActionLoading = false,
                            profile = currentState.profile?.copy(
                                relationStatus = actionResult.relationStatus,
                                hasPendingIncomingRequest = actionResult.hasPendingIncomingRequest,
                            )
                        )
                    }
                }

                is ResultWrapper.Error -> {
                    _profileState.update {
                        it.copy(
                            isActionLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }
}
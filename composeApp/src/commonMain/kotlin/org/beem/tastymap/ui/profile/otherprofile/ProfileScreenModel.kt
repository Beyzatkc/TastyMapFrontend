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
import org.beem.tastymap.domain.usecase.ToggleBlockUseCase
import org.beem.tastymap.domain.usecase.ToggleFollowUseCase
import org.beem.tastymap.ui.profile.otherprofile.ProfileUiState


class ProfileScreenModel(
    private val repo: ProfileRepository,
    private val toggleFollowUseCase: ToggleFollowUseCase,
    private val toggleBlockUseCase: ToggleBlockUseCase,
    private val userManager: UserManager

): ScreenModel {

    private val _profileState = MutableStateFlow(ProfileUiState())
    val profileState = _profileState.asStateFlow()

    private var profileJob: Job? = null


    fun getProfile(userId: Long) {
        if (profileJob?.isActive == true) return

        profileJob = screenModelScope.launch {
            _profileState.update {
                it.copy(isLoading = it.profile == null, errorMessage = null)
            }

            repo.getMyProfile(userId).collect { result ->
                when (result) {
                    is ResultWrapper.Success -> {
                        _profileState.update {
                            it.copy(
                                isLoading = false,
                                profile = result.data,
                                errorMessage = null
                            )
                        }
                    }
                    is ResultWrapper.Error -> {
                        _profileState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun toggleBlockStatus(targetUserId: Long) {
        val currentProfile = _profileState.value.profile ?: return

        screenModelScope.launch {
            _profileState.update { it.copy(isActionLoading = true, errorMessage = null) }

            val myUserId = userManager.getUserId() ?: return@launch

            val result = toggleBlockUseCase(
                targetUserId = targetUserId,
                myUserId = myUserId,
                isCurrentlyBlocked = currentProfile.blockedByMe,
                currentRelationStatus = currentProfile.relationStatus,
                isFollower = currentProfile.isFollower
            )

            when (result) {
                is ResultWrapper.Success -> {
                    _profileState.update { it.copy(isActionLoading = false) }
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

    fun refreshProfile(userId: Long) {
        screenModelScope.launch {
            _profileState.update { it.copy(isRefreshing = true, errorMessage = null) }

            try {
                repo.fetchRemoteProfile(userId)
                _profileState.update { it.copy(isRefreshing = false) }

            } catch (e: Exception) {
                _profileState.update {
                    it.copy(isRefreshing = false, errorMessage = e.message ?: "Yenilenirken bir hata oluştu")
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

    fun removeFollower(targetUserId: Long) {
        executeAction(
            targetUserId = targetUserId,
            action = ToggleFollowUseCase.Action.RemoveFollower
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
                                isFollower = actionResult.isFollower
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
package org.beem.tastymap.ui.profile.otherprofile
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.data.repository.profile.ProfileRepository
import org.beem.tastymap.domain.model.RelationStatus
import org.beem.tastymap.domain.usecase.ToggleBlockUseCase
import org.beem.tastymap.domain.usecase.ToggleFollowUseCase


class ProfileScreenModel(
    private val userId: Long,
    private val repo: ProfileRepository,
    private val toggleFollowUseCase: ToggleFollowUseCase,
    private val toggleBlockUseCase: ToggleBlockUseCase,

): ScreenModel {

    private val _profileState = MutableStateFlow(ProfileUiState())
    val profileState = _profileState.asStateFlow()

    private var observeJob: Job? = null

    init {
        observeProfile(userId)
        fetchRemoteProfile()
    }
    private fun observeProfile(userId:Long) {
        observeJob = screenModelScope.launch {
            repo.getProfile(userId)
                .distinctUntilChanged()
                .collect { profile ->

                    _profileState.update {
                        it.copy(
                            profile = profile,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
        }
    }

    fun fetchRemoteProfile() {
        screenModelScope.launch {
            when (val result = repo.refreshProfile(userId)) {

                is ResultWrapper.Success -> {
                    _profileState.update {
                        it.copy(
                            isLoading = false,
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

    fun refreshProfile() {

        if (_profileState.value.isRefreshing) {
            return
        }

        screenModelScope.launch {

            _profileState.update {
                it.copy(isRefreshing = true)
            }

            when (val result = repo.refreshProfile(userId)) {

                is ResultWrapper.Success -> {
                    _profileState.update {
                        it.copy(
                            profile = result.data,
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = null
                        )
                    }
                }

                is ResultWrapper.Error -> {
                    _profileState.update {
                        it.copy(
                            isRefreshing = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    fun clearError() {
        _profileState.update {
            it.copy(errorMessage = null)
        }
    }
    fun toggleBlockStatus(targetUserId: Long) {
        val currentProfile = _profileState.value.profile ?: return

        screenModelScope.launch {
            _profileState.update { it.copy(isActionLoading = true, errorMessage = null) }

            val result = toggleBlockUseCase(
                targetUserId = targetUserId,
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
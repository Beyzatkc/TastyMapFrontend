package org.beem.tastymap.ui.profile.otherprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

class ProfileViewModel(
    private val initialUserId: Long?,
    private val initialUsername: String?,
    private val repo: ProfileRepository,
    private val toggleFollowUseCase: ToggleFollowUseCase,
    private val toggleBlockUseCase: ToggleBlockUseCase,
): ViewModel() {

    private val _profileState = MutableStateFlow(ProfileUiState())
    val profileState = _profileState.asStateFlow()

    private var observeJob: Job? = null


    var activeUserId: Long? = initialUserId
        private set

    init {
        if (initialUserId != null) {
            observeProfile(initialUserId)
            refreshProfile(initialUserId)
        } else if (!initialUsername.isNullOrBlank()) {
            resolveUserAndObserve(initialUsername)
        }
    }

    private fun resolveUserAndObserve(username: String) {
        viewModelScope.launch {
            _profileState.update { it.copy(isLoading = true) }

            when (val result = repo.fetchProfileByUsername(username)) {
                is ResultWrapper.Success -> {
                    val resolvedUserId = result.data.userId
                    activeUserId = resolvedUserId // ID'yi yakaladık!

                    observeProfile(resolvedUserId)

                    _profileState.update { it.copy(isLoading = false, errorMessage = null) }
                }
                is ResultWrapper.Error -> {
                    _profileState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }

    private fun observeProfile(userId: Long) {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
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

    // Overload (Yardımcı private metod): Direkt ID alıp refresh atar
    private fun refreshProfile(userId: Long) {
        viewModelScope.launch {
            repo.refreshProfile(userId)
        }
    }


    // Swipe to Refresh (Kullanıcı ekrandan aşağı çekince çalışır)
    fun refreshProfile() {
        val currentId = activeUserId ?: return

        if (_profileState.value.isRefreshing) {
            return
        }

        viewModelScope.launch {
            _profileState.update {
                it.copy(isRefreshing = true)
            }

            when (val result = repo.refreshProfile(currentId)) {
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

        viewModelScope.launch {
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
        viewModelScope.launch {
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
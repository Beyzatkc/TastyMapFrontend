package org.beem.tastymap.ui.profile.myprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.data.repository.profile.MyProfileRepository

class MyProfileViewModel(
    private val repo: MyProfileRepository,
) : ViewModel() {

    private val _myProfileState = MutableStateFlow(MyProfileUiState())
    val myProfileState = _myProfileState.asStateFlow()

    private var profileJob: Job? = null

    fun getMyProfile() {
        profileJob?.cancel()

        profileJob = viewModelScope.launch {

            repo.getMyProfile().collect { profile ->

                _myProfileState.update {
                    it.copy(
                        profile = profile,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }
        }
    }

    fun fetchRemoteProfile(){
        viewModelScope.launch {
            when (val result = repo.refreshMyProfile()) {

                is ResultWrapper.Success -> {
                    _myProfileState.update {
                        it.copy(
                            isRefreshing = false,
                            isLoading = false,
                            profile = result.data,
                            errorMessage = null
                        )
                    }
                }

                is ResultWrapper.Error -> {
                    _myProfileState.update {
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

    fun refreshMyProfile() {
        if (_myProfileState.value.isRefreshing) return

        viewModelScope.launch {

            _myProfileState.update {
                it.copy(isRefreshing = true)
            }

            when (val result = repo.refreshMyProfile()) {

                is ResultWrapper.Success -> {
                    _myProfileState.update {
                        it.copy(
                            isRefreshing = false,
                            profile = result.data,
                            errorMessage = null
                        )
                    }
                }

                is ResultWrapper.Error -> {
                    _myProfileState.update {
                        it.copy(
                            isRefreshing = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    fun clearMessagesProfile() {
        _myProfileState.update { it.copy(errorMessage = null, successMessageRes = null) }
    }

}
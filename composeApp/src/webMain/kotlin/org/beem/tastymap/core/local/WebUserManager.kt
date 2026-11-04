package org.beem.tastymap.core.local

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WebUserManager : UserManager {

    private val _userSession = MutableStateFlow<UserSession?>(null)
    override val userSession: StateFlow<UserSession?> = _userSession.asStateFlow()

    override fun saveUser(userSession: UserSession) {
        _userSession.value = userSession
    }

    override fun updateProfileSession(
        username: String?,
        name: String?,
        surname: String?,
        profilePhoto: String?,
        biography: String?
    ) {
        val current = _userSession.value ?: return
        _userSession.value = current.copy(
            username = username ?: current.username,
            name = name ?: current.name,
            surname = surname ?: current.surname,
            profile = profilePhoto ?: current.profile,
            biography = biography ?: current.biography
        )
    }

    override fun setOnBoardComplete(completed: Boolean) {
        val current = _userSession.value ?: return
        _userSession.value = current.copy(onBoardComplete = completed)
    }

    override fun clear() {
        _userSession.value = null
    }
}
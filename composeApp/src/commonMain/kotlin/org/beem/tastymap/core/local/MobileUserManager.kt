package org.beem.tastymap.core.local

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MobileUserManager(private val settings: Settings) : UserManager {

    private val _userSession = MutableStateFlow<UserSession?>(getUserFromSettings())
    override val userSession: StateFlow<UserSession?> = _userSession.asStateFlow()

    private fun getUserFromSettings(): UserSession? {
        val id = settings.get<Long>(KEY_USER_ID) ?: return null
        return UserSession(
            userId = id,
            status = settings[KEY_STATUS],
            message = settings[KEY_MESSAGE],
            username = settings[KEY_USERNAME],
            name = settings[KEY_NAME],
            surname = settings[KEY_SURNAME],
            profile = settings[KEY_PROFILE],
            role = settings[KEY_ROLE],
            date = settings[KEY_DATE],
            biography = settings[KEY_BIOGRAPHY],
            onBoardComplete = settings[KEY_ON_BOARD_COMPLETE]
        )
    }

    override fun saveUser(userSession: UserSession) {
        settings[KEY_USER_ID] = userSession.userId
        settings[KEY_USERNAME] = userSession.username
        settings[KEY_NAME] = userSession.name
        settings[KEY_SURNAME] = userSession.surname
        settings[KEY_PROFILE] = userSession.profile
        settings[KEY_ROLE] = userSession.role
        settings[KEY_DATE] = userSession.date
        settings[KEY_BIOGRAPHY] = userSession.biography
        settings[KEY_ON_BOARD_COMPLETE] = userSession.onBoardComplete
        settings[KEY_STATUS] = userSession.status
        settings[KEY_MESSAGE] = userSession.message

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
        val updated = current.copy(
            username = username ?: current.username,
            name = name ?: current.name,
            surname = surname ?: current.surname,
            profile = profilePhoto ?: current.profile,
            biography = biography ?: current.biography
        )
        saveUser(updated)
    }

    override fun setOnBoardComplete(completed: Boolean) {
        val current = _userSession.value ?: return
        val updated = current.copy(onBoardComplete = completed)
        saveUser(updated)
    }

    override fun clear() {
        ALL_USER_KEYS.forEach { key ->
            settings.remove(key)
        }
        _userSession.value = null
    }

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_NAME = "name"
        private const val KEY_SURNAME = "surname"
        private const val KEY_PROFILE = "profile"
        private const val KEY_ROLE = "role"
        private const val KEY_DATE = "date"
        private const val KEY_BIOGRAPHY = "biography"
        private const val KEY_ON_BOARD_COMPLETE = "boardComplete"
        private const val KEY_STATUS = "status"
        private const val KEY_MESSAGE = "message"
        private val ALL_USER_KEYS = listOf(
            KEY_USER_ID, KEY_USERNAME, KEY_NAME, KEY_SURNAME,
            KEY_PROFILE, KEY_ROLE, KEY_DATE, KEY_BIOGRAPHY,
            KEY_ON_BOARD_COMPLETE, KEY_STATUS, KEY_MESSAGE
        )
    }
}
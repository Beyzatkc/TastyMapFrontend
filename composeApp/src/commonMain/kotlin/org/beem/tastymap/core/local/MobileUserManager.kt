package org.beem.tastymap.core.local
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import com.russhwolf.settings.get

class MobileUserManager(private val settings: Settings): UserManager{
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
    }

    override fun getStatus(): String? = settings[KEY_STATUS]
    override fun getMessage(): String? = settings[KEY_MESSAGE]
    override fun getUsername(): String? = settings[KEY_USERNAME]
    override fun getUserId(): Long? = settings[KEY_USER_ID]
    override fun getName(): String? = settings[KEY_NAME]
    override fun getSurname(): String? = settings[KEY_SURNAME]
    override fun getProfile(): String? = settings[KEY_PROFILE]
    override fun getRole(): String? = settings[KEY_ROLE]
    override fun getDate(): String? = settings[KEY_DATE]
    override fun getBiography(): String? = settings[KEY_BIOGRAPHY]
    override fun getOnBoardComplete(): Boolean? = settings[KEY_ON_BOARD_COMPLETE]

    override fun clear() {
       settings.clear()
    }


}
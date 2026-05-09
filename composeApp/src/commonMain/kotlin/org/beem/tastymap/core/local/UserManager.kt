package org.beem.tastymap.core.local

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import com.russhwolf.settings.get


interface UserManager {
    fun saveUser(
        status: String?,
        message: String?,
        userId: Long?,
        username: String?,
        name: String?,
        surname: String?,
        profile: String?,
        role: String?,
        date: String?,
        biography: String?,
    )
    fun getStatus(): String?
    fun getMessage(): String?
    fun getUserId(): Long?
    fun getUsername(): String?
    fun getName(): String?
    fun getSurname(): String?
    fun getProfile(): String?
    fun getRole(): String?
    fun getDate(): String?
    fun getBiography(): String?
    fun clear()
}

class UserManagerImpl(private val settings: Settings): UserManager{
    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_NAME = "name"
        private const val KEY_SURNAME = "surname"
        private const val KEY_PROFILE = "profile"
        private const val KEY_ROLE = "role"
        private const val KEY_DATE = "date"
        private const val KEY_BIOGRAPHY = "biography"
        private const val KEY_STATUS = "status"
        private const val KEY_MESSAGE = "message"
    }
    override fun saveUser(
        status: String?,
        message: String?,
        userId: Long?,
        username: String?,
        name: String?,
        surname: String?,
        profile: String?,
        role: String?,
        date: String?,
        biography: String?
    ) {
        settings[KEY_USER_ID] = userId
        settings[KEY_USERNAME] = username
        settings[KEY_NAME] = name
        settings[KEY_SURNAME] = surname
        settings[KEY_PROFILE] = profile
        settings[KEY_ROLE] = role
        settings[KEY_DATE] = date
        settings[KEY_BIOGRAPHY] = biography
        settings[KEY_STATUS] = status
        settings[KEY_MESSAGE] = message
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

    override fun clear() {
       settings.clear()
    }


}
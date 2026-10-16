package org.beem.tastymap.core.local

import kotlinx.coroutines.flow.StateFlow

interface UserManager {
    val userSession: StateFlow<UserSession?>

    fun saveUser(userSession: UserSession)
    fun updateProfileSession(
        username: String?,
        name: String?,
        surname: String?,
        profilePhoto: String?,
        biography: String?
    )
    fun setOnBoardComplete(completed: Boolean)
    fun clear()
}
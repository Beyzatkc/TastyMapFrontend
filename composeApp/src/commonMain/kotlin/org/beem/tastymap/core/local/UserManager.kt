package org.beem.tastymap.core.local

interface UserManager {
    fun saveUser(
       userSession: UserSession
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
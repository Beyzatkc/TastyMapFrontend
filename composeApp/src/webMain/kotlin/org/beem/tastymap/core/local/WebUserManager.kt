package org.beem.tastymap.core.local

class WebUserManager(): UserManager {
    private var currentUserSession: UserSession? = null

    override fun saveUser(userSession: UserSession) {
        this.currentUserSession = userSession
    }

    override fun getStatus(): String? = currentUserSession?.status
    override fun getMessage(): String? = currentUserSession?.message
    override fun getUserId(): Long? = currentUserSession?.userId
    override fun getUsername(): String? = currentUserSession?.username
    override fun getName(): String? = currentUserSession?.name
    override fun getSurname(): String? = currentUserSession?.surname
    override fun getProfile(): String? = currentUserSession?.profile
    override fun getRole(): String? = currentUserSession?.role
    override fun getDate(): String? = currentUserSession?.date
    override fun getBiography(): String? = currentUserSession?.biography

    override fun clear() {
        this.currentUserSession = null
    }
}
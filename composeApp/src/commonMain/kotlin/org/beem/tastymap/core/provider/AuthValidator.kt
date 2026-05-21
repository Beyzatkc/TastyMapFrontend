package org.beem.tastymap.core.provider

interface AuthValidator {
    suspend fun isUserLoggedIn(): Boolean
}
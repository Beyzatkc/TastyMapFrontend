package org.beem.tastymap.core.provider

import org.beem.tastymap.data.model.auth.AuthStatus

interface AuthValidator {
    suspend fun getAuthStatus(): AuthStatus
}
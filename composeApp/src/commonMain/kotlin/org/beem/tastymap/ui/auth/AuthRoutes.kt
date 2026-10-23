package org.beem.tastymap.ui.auth

import kotlinx.serialization.Serializable

@Serializable data object AuthGraph

@Serializable data object LogRegRoute

@Serializable data object ForgotRoute

@Serializable
data class ResetRoute(val token: String)

@Serializable data object ChangeSuccessRoute

@Serializable
data class EmailRoute(val email: String, val deviceId: String, val userId: Long)
@Serializable
data class VerifyRoute(val token: String)
@Serializable data object VerificationSuccessRoute
@Serializable
data class LoginPendingRoute(val deviceId: String)
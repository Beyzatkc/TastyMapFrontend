package org.beem.tastymap.data.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val username:String,
    val name:String,
    val surname:String,
    val email:String,
    val password:String,
    val profile:String?,
    val biography:String?,
    val role:String,
    val privateProfile: Boolean,
    val deviceId: String
)

@Serializable
data class UserResponse(
    val id:Long,
    val username:String,
    val email:String,
    val name:String,
    val surname:String,
    val profile:String?,
    val role:String,
    val biography:String?,
    val date:String,
    val emailVerified: Boolean,
    val onboardingCompleted: Boolean,
    val privateProfile: Boolean
)
@Serializable
enum class LoginStatus {
    SUCCESS,
    PENDING_SECURITY
}

@Serializable
data class LoginRequest(
    val username:String,
    val password:String,
    val deviceId:String,
    val fcmToken:String
)
@Serializable
data class LoginResponse(
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val userResponseDTO: UserResponse? = null,
    val status: LoginStatus? = null,
    val message: String? = null
)




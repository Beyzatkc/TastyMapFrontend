package org.beem.tastymap.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenResponseDTO(
    val accessToken: String,
    val refreshToken: String,
    val message: String,
)
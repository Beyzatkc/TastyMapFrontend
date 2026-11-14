package org.beem.tastymap.data.model.ai

import kotlinx.serialization.Serializable

@Serializable
data class UserQuotaStatusDto(
    val userId: Long,
    val dailyLimit: Int,
    val usedToday: Int,
    val remainingTokens: Int,
    val hasQuota: Boolean,
    val remainingPercentage: Double,
    val lifetimeSpentTry: Double,
    val resetDate: String
)
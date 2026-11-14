package org.beem.tastymap.ui.ai.model

data class AiQuotaState(
    val dailyLimit: Int = 50_000,
    val usedToday: Int = 0,
    val remainingTokens: Int = 50_000,
    val remainingPercentage: Double = 100.0,
    val hasQuota: Boolean = true,
    val resetDate: String = ""
) {
    val remainingProgress: Float
        get() = if (dailyLimit > 0) (remainingTokens.toFloat() / dailyLimit).coerceIn(0f, 1f) else 0f

    val isLowQuota: Boolean
        get() = remainingPercentage <= 20.0
}
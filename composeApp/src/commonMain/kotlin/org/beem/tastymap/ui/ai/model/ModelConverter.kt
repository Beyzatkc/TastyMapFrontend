package org.beem.tastymap.ui.ai.model

import org.beem.tastymap.data.model.ai.QuotaFeedbackPayloadDto
import org.beem.tastymap.data.model.ai.UserQuotaStatusDto


fun UserQuotaStatusDto.toUiState() = AiQuotaState(
    dailyLimit = dailyLimit,
    usedToday = usedToday,
    remainingTokens = remainingTokens,
    remainingPercentage = remainingPercentage,
    hasQuota = hasQuota,
    resetDate = resetDate
)

fun AiQuotaState.updateFromStream(payload: QuotaFeedbackPayloadDto): AiQuotaState {
    val activeLimit = if (payload.dailyTokenLimit > 0) payload.dailyTokenLimit else dailyLimit
    val newRemaining = (activeLimit - payload.tokensUsedToday).coerceAtLeast(0)

    return copy(
        dailyLimit = activeLimit,
        usedToday = payload.tokensUsedToday,
        remainingTokens = newRemaining,
        remainingPercentage = payload.remainingPercentage.coerceIn(0.0, 100.0),
        hasQuota = newRemaining > 0
    )
}
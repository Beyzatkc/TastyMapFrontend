package org.beem.tastymap.data.model.ai.summary

import kotlinx.serialization.Serializable

@Serializable
data class AiSummaryResponse(
    val hasSummary: Boolean = false,
    val summaryText: String? = null
)
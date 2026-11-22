package org.beem.tastymap.data.model.report

import kotlinx.serialization.Serializable

@Serializable
enum class ReportTargetType {
    USER,
    POST,
    COMMENT
}

@Serializable
enum class ReportReason {
    SPAM,
    IMPERSONATION,
    INAPPROPRIATE_CONTENT,
    HARASSMENT,
    SCAM,
    FALSE_INFO,
    OTHER
}

@Serializable
data class CreateReportRequest(
    val targetId: Long,
    val targetType: ReportTargetType,
    val reason: ReportReason,
    val description: String? = null
)

package org.beem.tastymap.data.model.deleteaccount

import kotlinx.serialization.Serializable

@Serializable
enum class DeleteReason {
    NOT_USING,
    APP_IS_SLOW,
    TECHNICAL_ISSUES,
    PRIVACY_CONCERNS,
    POOR_RECOMMENDATIONS,
    MISSING_FEATURES,
    DIFFICULT_TO_USE,
    FOUND_AN_ALTERNATIVE,
    OTHER
}

@Serializable
data class DeleteAccountRequest(
    val password: String,
    val reasonType: DeleteReason?,
    val customReason: String? = null
)


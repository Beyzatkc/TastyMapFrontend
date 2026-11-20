package org.beem.tastymap.data.model.support

import kotlinx.serialization.Serializable

@Serializable
data class CreateSupportTicketRequest(
    val category: SupportCategory,
    val message: String,
    val appVersion: String? = null,
    val deviceInfo: String? = null
)
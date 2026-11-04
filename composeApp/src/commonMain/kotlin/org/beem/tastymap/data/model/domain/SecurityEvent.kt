package org.beem.tastymap.data.model.domain

import org.beem.tastymap.data.model.remote.SecurityEventDTO

data class SecurityEvent(
    val type: SecurityEventType,
    val message: String
)

fun SecurityEventDTO.toDomain(): SecurityEvent {
    return SecurityEvent(
        type = SecurityEventType.valueOf(this.type),
        message = this.message
    )
}
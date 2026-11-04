package org.beem.tastymap.data.mapper
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.beem.tastymap.data.model.health.AllergyInfo

fun List<AllergyInfo>?.toJsonString(): String? {
    if (this == null) return null
    return Json.encodeToString(this)
}

fun String?.toAllergyList(): List<AllergyInfo> {
    if (this.isNullOrBlank()) return emptyList()
    return try {
        Json.decodeFromString(this)
    } catch (e: Exception) {
        emptyList()
    }
}
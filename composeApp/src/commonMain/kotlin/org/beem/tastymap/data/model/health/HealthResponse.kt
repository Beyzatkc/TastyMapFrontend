package org.beem.tastymap.data.model.health

import kotlinx.serialization.Serializable

@Serializable
data class HealthRequest(
    val hasDiabetes: Boolean?,
    val eatType: HealthEnum?,
    val allergyIds: List<Long>?
)

@Serializable
enum class HealthEnum {
    VEGETARIAN,
    VEGAN,
    NORMAL
}
@Serializable
data class HealthResponse(
    val hasDiabetes: Boolean?,
    val eatType: String?,
    val allergyInfo: List<AllergyInfo>?
)
@Serializable
data class AllergyInfo(
    val id: Long,
    val name: String,
)
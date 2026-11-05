package org.beem.tastymap.map.model

import kotlinx.serialization.Serializable

@Serializable
data class Geometry(
    val type: String,
    val coordinates: DoubleArray
){
    val longitude: Double get() = coordinates[0]
    val latitude: Double get() = coordinates[1]

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Geometry) return false
        return type == other.type && coordinates.contentEquals(other.coordinates)
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + coordinates.contentHashCode()
        return result
    }
}

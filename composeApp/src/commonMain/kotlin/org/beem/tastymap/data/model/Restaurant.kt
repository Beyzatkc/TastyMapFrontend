package org.beem.tastymap.data.model

data class Restaurant(
    val id: String,
    val name: String,
    val cuisine: String,
    val latitude: Double,
    val longitude: Double,
    val rating: Double
)
package org.beem.tastymap.data.model

data class Restaurant(
    val id: String,
    val name: String,
    var address: String,
    var latitude: Double,
    var longitude: Double,
    var rating: Double? = null,
    var status: String,
    var totalRatings: Int? = null,
    var types: List<String>? = null,
    val category: String,
)
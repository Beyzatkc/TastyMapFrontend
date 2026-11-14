package org.beem.tastymap.ui.map.states

data class RouteRenderData(
    val mainRoute: List<List<Double>>,
    val startConnector: List<List<Double>>,
    val endConnector: List<List<Double>>,
    val targetPlaceId: String
)
package org.beem.tastymap.place.state

import org.beem.tastymap.place.model.details.PlaceDetailsResult

data class PlaceDetailsUiState(
    val isLoading: Boolean = false,
    val details: PlaceDetailsResult? = null,
    val errorMessage: String? = null,
    val quickScore: Double = 0.0,
    val isAddReviewOpen: Boolean = false
)
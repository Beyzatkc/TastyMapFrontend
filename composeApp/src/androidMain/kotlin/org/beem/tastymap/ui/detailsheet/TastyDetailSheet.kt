package org.beem.tastymap.ui.detailsheet

import androidx.compose.runtime.*
import org.beem.tastymap.data.model.Restaurant
import org.beem.tastymap.place.RestaurantDetailScreenModel
import org.beem.tastymap.place.state.RestaurantDetailIntent
import org.beem.tastymap.ui.review.AddReviewBottomSheet
import org.koin.compose.koinInject

@Composable
actual fun TastyDetailSheet(
    restaurant: Restaurant,
    onDirectionsClick: () -> Unit,
    onDismiss: () -> Unit,
    collapseToPeekTrigger: Int
) {
    val detailScreenModel: RestaurantDetailScreenModel = koinInject()
    val pagingState by detailScreenModel.reviewsPagingState.collectAsState()
    val detailsUiState by detailScreenModel.detailsUiState.collectAsState()

    // Mekan değiştikçe yorumları yükle
    LaunchedEffect(restaurant.id) {
        detailScreenModel.loadPlaceData(restaurant.id)
    }

    TastyDetailSheetContent(
        restaurant = restaurant,
        detailsUiState = detailsUiState,
        pagingState = pagingState,
        collapseToPeekTrigger = collapseToPeekTrigger,
        onIntent = { intent ->
            if (intent is RestaurantDetailIntent.DismissMainSheet) {
                detailScreenModel.handleIntent(intent)
                onDismiss()
            }
            else if (intent is RestaurantDetailIntent.StartDirections) {
                onDirectionsClick()
            } else {
                detailScreenModel.handleIntent(intent)
            }
        }
    )

    // 2. Detaylı Yorum ve Puanlama Sheet'i
    if (detailsUiState.isAddReviewOpen) {
        AddReviewBottomSheet(
            placeId = restaurant.id,
            restaurantName = restaurant.name,
            initialMainScore = detailsUiState.quickScore,
            existingReview = detailsUiState.details?.userReview,
            onDismiss = {
                detailScreenModel.handleIntent(RestaurantDetailIntent.DismissAddReview)
            },
            onReviewSubmittedSuccessfully = { review, userSummary ->
                detailScreenModel.handleIntent(
                    RestaurantDetailIntent.ReviewCreatedLocally(review, userSummary)
                )
            },
            onReviewUpdatedSuccessfully = { review, userSummary ->
                detailScreenModel.handleIntent(
                    RestaurantDetailIntent.ReviewUpdatedLocally(review, userSummary)
                )
            },
            onReviewDeletedSuccessfully = { reviewId, score ->
                detailScreenModel.handleIntent(
                    RestaurantDetailIntent.ReviewDeletedLocally(reviewId, score)
                )
            }
        )
    }
}
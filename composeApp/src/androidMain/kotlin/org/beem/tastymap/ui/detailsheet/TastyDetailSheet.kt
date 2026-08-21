package org.beem.tastymap.ui.detailsheet

import androidx.compose.runtime.*
import org.beem.tastymap.data.model.Restaurant
import org.beem.tastymap.place.RestaurantDetailScreenModel
import org.beem.tastymap.ui.review.AddReviewBottomSheet
import org.koin.compose.koinInject

@Composable
actual fun TastyDetailSheet(
    restaurant: Restaurant,
    onDismiss: () -> Unit
) {
    val detailScreenModel: RestaurantDetailScreenModel = koinInject()
    val pagingState by detailScreenModel.reviewsPagingState.collectAsState()
    val placeDetails by detailScreenModel.placeDetails.collectAsState()

    var showAddReviewSheet by remember { mutableStateOf(false) }
    var selectedInitialScore by remember { mutableStateOf(0.0) }

    // Mekan değiştikçe yorumları yükle
    LaunchedEffect(restaurant.id) {
        detailScreenModel.loadPlaceData(restaurant.id)
    }

    // 1. Mekan Detay Sheet İçeriği
    TastyDetailSheetContent(
        restaurant = restaurant,
        placeDetails = placeDetails,
        pagingState = pagingState,
        onLoadMoreReviews = { detailScreenModel.loadMoreReviews() },
        onAddReviewClick = { initialScore ->
            selectedInitialScore = initialScore
            showAddReviewSheet = true // Mekan sheet'i kapanmadan üstüne biner
        },
        onDismiss = onDismiss
    )

    // 2. Detaylı Yorum ve Puanlama Sheet'i
    if (showAddReviewSheet) {
        AddReviewBottomSheet(
            placeId = restaurant.id,
            restaurantName = restaurant.name,
            initialMainScore = selectedInitialScore,
            onDismiss = {
                showAddReviewSheet = false
            },
            onReviewSubmittedSuccessfully = {
                showAddReviewSheet = false
                detailScreenModel.loadPlaceData(restaurant.id)
            }
        )
    }
}
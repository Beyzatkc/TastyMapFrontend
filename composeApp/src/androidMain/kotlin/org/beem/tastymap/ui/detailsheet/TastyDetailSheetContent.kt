package org.beem.tastymap.ui.detailsheet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.beem.tastymap.core.paging.TastyPagingState
import org.beem.tastymap.data.model.Restaurant
import org.beem.tastymap.place.model.details.PlaceDetailsResult
import org.beem.tastymap.place.model.review.ReviewItem
import org.beem.tastymap.ui.detailsheet.components.QuickReviewCard
import org.beem.tastymap.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TastyDetailSheetContent(
    restaurant: Restaurant,
    placeDetails: PlaceDetailsResult?,
    pagingState: TastyPagingState<ReviewItem>,
    onLoadMoreReviews: () -> Unit,
    onAddReviewClick: (initialScore: Double) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val listState = rememberLazyListState()
    var quickScore by remember { mutableStateOf(0.0) }

    val shouldLoadMore = remember {
        derivedStateOf {
            val totalItems = listState.layoutInfo.totalItemsCount
            val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            totalItems > 0 && lastVisibleItemIndex >= totalItems - 2
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value && !pagingState.isLoading && !pagingState.isEndReached) {
            onLoadMoreReviews()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AppColors.BackBackgroundBlue,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = AppColors.DarkGrayLines.copy(alpha = 0.4f)
            )
        },
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Başlık & Mekan Bilgileri
            item {
                RestaurantHeaderSection(restaurant = restaurant)
            }

            // Hızlı Puanlama Kartı
            item {
                val myReview = placeDetails?.userReview
                if (myReview != null) {
                    UserOwnReviewCard(
                        review = myReview,
                        onEditClick = {
                            onAddReviewClick(myReview.rating)
                        }
                    )
                } else {
                    QuickReviewCard(
                        userName = "Emrullah Uygun",
                        score = quickScore,
                        onScoreChange = { newScore -> quickScore = newScore },
                        onScoreSelected = { finalScore ->
                            quickScore = finalScore
                            onAddReviewClick(finalScore)
                        }
                    )
                }
            }

            item {
                HorizontalDivider(
                    color = AppColors.DarkGrayLines.copy(alpha = 0.2f),
                    thickness = 1.dp
                )
            }

            // Değerlendirmeler Başlığı & Sayaç
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Değerlendirmeler & Yorumlar",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.NavyBlue
                    )
                    if (pagingState.items.isNotEmpty()) {
                        Surface(
                            color = AppColors.WaveColor,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "${pagingState.items.size} Yorum",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppColors.NavyBlue
                            )
                        }
                    }
                }
            }

            // Boş Durum
            if (pagingState.items.isEmpty() && !pagingState.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Henüz değerlendirme bulunmuyor.",
                            color = AppColors.DarkGrayLines,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                items(
                    items = pagingState.items,
                    key = { it.id }
                ) { review ->
                    ReviewItemCard(review = review)
                }
            }

            // Yükleme Animasyonu
            if (pagingState.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = AppColors.WarmAmber,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }
}
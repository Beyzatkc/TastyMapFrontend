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
import org.beem.tastymap.place.model.review.ReviewItem
import org.beem.tastymap.place.state.PlaceDetailsUiState
import org.beem.tastymap.place.state.RestaurantDetailIntent
import org.beem.tastymap.ui.theme.AppColors
import org.beem.tastymap.ui.theme.getAppFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TastyDetailSheetContent(
    restaurant: Restaurant,
    detailsUiState: PlaceDetailsUiState,
    pagingState: TastyPagingState<ReviewItem>,
    onIntent: (RestaurantDetailIntent) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val listState = rememberLazyListState()
    val fontFamily = getAppFontFamily()

    val shouldLoadMore = remember {
        derivedStateOf {
            val totalItems = listState.layoutInfo.totalItemsCount
            val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            totalItems > 0 && lastVisibleItemIndex >= totalItems - 2
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value && !pagingState.isLoading && !pagingState.isEndReached) {
            onIntent(RestaurantDetailIntent.LoadMoreReviews)
        }
    }

    ModalBottomSheet(
        onDismissRequest = { onIntent(RestaurantDetailIntent.DismissMainSheet) },
        sheetState = sheetState,
        containerColor = AppColors.Surface,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = AppColors.BorderStrong.copy(alpha = 0.4f)
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

            item {
                when {
                    detailsUiState.isLoading && detailsUiState.details == null -> {
                        // Detaylar ilk kez yüklenirken hafif bir placeholder/loader
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = AppColors.GourmetOrange,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    detailsUiState.errorMessage != null && detailsUiState.details == null -> {
                        // Detay yüklenemedi uyarısı
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Kullanıcı bilgisi yüklenemedi",
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.SemiBold,
                                color = AppColors.TextSecondary,
                                fontSize = 13.sp
                            )
                            TextButton(onClick = { onIntent(RestaurantDetailIntent.RetryDetails) }) {
                                Text(
                                    text = "Tekrar Dene",
                                    fontFamily = fontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AppColors.NavyBlue
                                )
                            }
                        }
                    }
                    else -> {
                        val myReview = detailsUiState.details?.userReview
                        if (myReview != null) {
                            UserOwnReviewCard(
                                review = myReview,
                                onEditClick = { onIntent(RestaurantDetailIntent.OpenAddReview(myReview.rating)) }
                            )
                        } else {
                            QuickReviewCard(
                                userName = "Emrullah Uygun",
                                score = detailsUiState.quickScore,
                                onScoreChange = { onIntent(RestaurantDetailIntent.QuickScoreChanged(it)) },
                                onScoreSelected = { finalScore ->
                                    onIntent(RestaurantDetailIntent.OpenAddReview(finalScore))
                                }
                            )
                        }
                    }
                }
            }

            item {
                HorizontalDivider(
                    color = AppColors.BorderLight,
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
                        fontFamily = fontFamily,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )
                    if (pagingState.items.isNotEmpty()) {
                        Surface(
                            color = AppColors.SurfaceVariant,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "${pagingState.items.size} Yorum",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontFamily = fontFamily,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
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
                            fontFamily = fontFamily,
                            color = AppColors.TextTertiary,
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
                            color = AppColors.GourmetOrange,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }
}
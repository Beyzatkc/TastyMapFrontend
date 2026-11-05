package org.beem.tastymap.ui.detailsheet

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.beem.tastymap.core.paging.TastyPagingState
import org.beem.tastymap.data.model.Restaurant
import org.beem.tastymap.place.model.review.ReviewItem
import org.beem.tastymap.place.state.PlaceDetailsUiState
import org.beem.tastymap.place.state.RestaurantDetailIntent
import org.beem.tastymap.ui.detailsheet.components.PeekHeaderSection
import org.beem.tastymap.ui.detailsheet.components.TastyDetailTabBar
import org.beem.tastymap.ui.detailsheet.model.DetailTabType
import org.beem.tastymap.ui.detailsheet.tabs.*
import org.beem.tastymap.ui.theme.AppColors
import org.beem.tastymap.ui.theme.getAppFontFamily
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TastyDetailSheetContent(
    restaurant: Restaurant,
    detailsUiState: PlaceDetailsUiState,
    pagingState: TastyPagingState<ReviewItem>,
    collapseToPeekTrigger: Int,
    onIntent: (RestaurantDetailIntent) -> Unit
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val coroutineScope = rememberCoroutineScope()

    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }

    // --- KADEMELER ---
    val peekHeightPx = with(density) { 128.dp.toPx() }
    val peekOffset = screenHeightPx - peekHeightPx
    val halfOffset = screenHeightPx * 0.52f
    val expandedOffset = with(density) { 38.dp.toPx() }

    val offsetY = remember { Animatable(screenHeightPx) }

    val snapAnimationSpec = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )

    LaunchedEffect(restaurant.id, screenHeightPx) {
        offsetY.animateTo(halfOffset, animationSpec = tween(320))
    }

    LaunchedEffect(collapseToPeekTrigger) {
        if (collapseToPeekTrigger > 0 && offsetY.value < peekOffset) {
            offsetY.animateTo(peekOffset, animationSpec = tween(250))
        }
    }

    var dragStartY by remember { mutableStateOf(halfOffset) }

    fun resolveTargetSnap(currentY: Float, startY: Float): Float {
        val delta = currentY - startY
        val threshold = 30f

        return if (delta < -threshold) {
            if (startY > halfOffset + 30f) halfOffset else expandedOffset
        } else if (delta > threshold) {
            if (startY < halfOffset - 30f) halfOffset else peekOffset
        } else {
            val midPoint3to2 = (expandedOffset + halfOffset) / 2f
            val midPoint2to1 = (halfOffset + peekOffset) / 2f
            when {
                currentY < midPoint3to2 -> expandedOffset
                currentY < midPoint2to1 -> halfOffset
                else -> peekOffset
            }
        }
    }

    val isPeekMode = offsetY.value >= (peekOffset - with(density) { 30.dp.toPx() })
    val isExpanded = offsetY.value <= (expandedOffset + with(density) { 6.dp.toPx() })

    val listState = rememberLazyListState()
    val fontFamily = getAppFontFamily()
    var selectedTab by remember { mutableStateOf(DetailTabType.REVIEWS) }

    val nestedScrollConnection = remember(screenHeightPx) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (source == NestedScrollSource.UserInput && available.y < 0 && offsetY.value > expandedOffset) {
                    val newOffset = (offsetY.value + available.y).coerceAtLeast(expandedOffset)
                    val consumed = offsetY.value - newOffset
                    coroutineScope.launch { offsetY.snapTo(newOffset) }
                    return Offset(0f, -consumed)
                }
                return Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                val isListAtTop = listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
                if (source == NestedScrollSource.UserInput && available.y > 0 && isListAtTop) {
                    val newOffset = (offsetY.value + available.y).coerceAtMost(peekOffset)
                    val consumedY = newOffset - offsetY.value
                    coroutineScope.launch { offsetY.snapTo(newOffset) }
                    return Offset(0f, consumedY)
                }
                return Offset.Zero
            }

            // Parmak ekrandayken erken tetiklenmeyi önlemek için onPreFling boş geçilir
            override suspend fun onPreFling(available: Velocity): Velocity = Velocity.Zero

            // Yalnızca parmak ekrandan tamamen çekildiğinde hedef kademeye animasyon başlatılır
            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                if (offsetY.value > expandedOffset && offsetY.value < peekOffset) {
                    val target = resolveTargetSnap(offsetY.value, expandedOffset)
                    offsetY.animateTo(target, animationSpec = snapAnimationSpec)
                    return available
                }
                return Velocity.Zero
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .offset { IntOffset(0, offsetY.value.roundToInt()) },
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = AppColors.Surface,
            shadowElevation = 6.dp,
            border = BorderStroke(0.8.dp, AppColors.BorderLight)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Drag Handle
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .pointerInput(screenHeightPx) {
                            detectVerticalDragGestures(
                                onDragStart = { dragStartY = offsetY.value },
                                onVerticalDrag = { change, dragAmount ->
                                    change.consume()
                                    coroutineScope.launch {
                                        val target = (offsetY.value + dragAmount).coerceIn(expandedOffset, peekOffset)
                                        offsetY.snapTo(target)
                                    }
                                },
                                onDragEnd = {
                                    val target = resolveTargetSnap(offsetY.value, dragStartY)
                                    coroutineScope.launch {
                                        offsetY.animateTo(target, animationSpec = snapAnimationSpec)
                                    }
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    BottomSheetDefaults.DragHandle(
                        color = AppColors.BorderStrong.copy(alpha = 0.4f)
                    )
                }

                Crossfade(targetState = isPeekMode, animationSpec = tween(180)) { inPeek ->
                    if (inPeek) {
                        PeekHeaderSection(
                            restaurant = restaurant,
                            detailsUiState = detailsUiState,
                            isSaved = false,
                            onCloseClick = { onIntent(RestaurantDetailIntent.DismissMainSheet) },
                            onDirectionsClick = { onIntent(RestaurantDetailIntent.StartDirections) },
                            onSaveClick = { },
                            onShareClick = { }
                        )
                    } else {
                        LazyColumn(
                            state = listState,
                            userScrollEnabled = isExpanded,
                            modifier = Modifier
                                .fillMaxSize()
                                .nestedScroll(nestedScrollConnection)
                                .then(
                                    if (!isExpanded) {
                                        Modifier.pointerInput(screenHeightPx) {
                                            detectVerticalDragGestures(
                                                onDragStart = { dragStartY = offsetY.value },
                                                onVerticalDrag = { change, dragAmount ->
                                                    change.consume()
                                                    coroutineScope.launch {
                                                        val target = (offsetY.value + dragAmount).coerceIn(expandedOffset, peekOffset)
                                                        offsetY.snapTo(target)
                                                    }
                                                },
                                                onDragEnd = {
                                                    val target = resolveTargetSnap(offsetY.value, dragStartY)
                                                    coroutineScope.launch {
                                                        offsetY.animateTo(target, animationSpec = snapAnimationSpec)
                                                    }
                                                }
                                            )
                                        }
                                    } else {
                                        Modifier
                                    }
                                ),
                            contentPadding = PaddingValues(
                                start = 20.dp,
                                end = 20.dp,
                                top = 6.dp,
                                bottom = 90.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                RestaurantHeaderSection(
                                    restaurant = restaurant,
                                    detailsUiState = detailsUiState,
                                    isSaved = false,
                                    onSaveClick = { },
                                    onDirectionsClick = { onIntent(RestaurantDetailIntent.StartDirections) }
                                )
                            }

                            item {
                                when {
                                    detailsUiState.isLoading && detailsUiState.details == null -> {
                                        Box(
                                            modifier = Modifier.fillMaxWidth().height(90.dp),
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
                                TastyDetailTabBar(
                                    selectedTab = selectedTab,
                                    onTabSelected = { selectedTab = it }
                                )
                            }

                            item {
                                HorizontalDivider(
                                    color = AppColors.BorderLight,
                                    thickness = 1.dp
                                )
                            }

                            when (selectedTab) {
                                DetailTabType.REVIEWS -> reviewsTabSection(pagingState = pagingState, fontFamily = fontFamily, onIntent = onIntent)
                                DetailTabType.STATS -> statsTabSection(details = detailsUiState.details, fontFamily = fontFamily)
                                DetailTabType.HOURS -> hoursTabSection(details = detailsUiState.details, fontFamily = fontFamily)
                                DetailTabType.MENU -> menuTabSection(fontFamily = fontFamily)
                            }
                        }
                    }
                }
            }
        }
    }
}
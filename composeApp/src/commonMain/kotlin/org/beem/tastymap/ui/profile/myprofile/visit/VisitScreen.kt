package org.beem.tastymap.ui.profile.myprofile.visit

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.beem.tastymap.core.util.ToastManager
import org.beem.tastymap.core.util.formatToRelativeDateTime
import org.beem.tastymap.data.model.visit.VisitResponse
import org.beem.tastymap.ui.components.DialogConfig
import org.beem.tastymap.ui.components.TastyConfirmDialog
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.active_devices_retry
import tastymap.composeapp.generated.resources.connection_error
import tastymap.composeapp.generated.resources.dialog_delete_visit_message
import tastymap.composeapp.generated.resources.profile_back_cd
import tastymap.composeapp.generated.resources.visit_history_empty
import tastymap.composeapp.generated.resources.visit_history_title
import tastymap.composeapp.generated.resources.visit_item_category_unspecified
import tastymap.composeapp.generated.resources.visit_item_delete_desc
import tastymap.composeapp.generated.resources.visit_item_rating_desc

class VisitScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<VisitScreenModel>()
        val uiState by screenModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val customColors = LocalCustomColors.current

        val pullToRefreshState = rememberPullToRefreshState()
        var activeDialog by remember { mutableStateOf<DialogConfig?>(null) }
        val listState = rememberLazyListState()

        LaunchedEffect(uiState.errorMessage) {
            uiState.errorMessage?.let { message ->
                ToastManager.show(message)
            }
        }

        val shouldLoadMore = remember {
            derivedStateOf {
                val totalItems = listState.layoutInfo.totalItemsCount
                val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                uiState.isInitialLoadCompleted &&
                        !uiState.isLoading &&
                        !uiState.isLoadingMore &&
                        !uiState.isLastPage &&
                        uiState.errorMessage == null &&
                        totalItems > 0 &&
                        lastVisibleItem >= totalItems - 2
            }
        }

        LaunchedEffect(shouldLoadMore.value) {
            if (shouldLoadMore.value) {
                screenModel.loadMore()
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(Res.string.profile_back_cd),
                                tint = customColors.textPrimary
                            )
                        }
                    },
                    title = {
                        Text(
                            text = stringResource(Res.string.visit_history_title),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = customColors.textPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = customColors.background,
                        scrolledContainerColor = customColors.background
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(customColors.background),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PullToRefreshBox(
                    state = pullToRefreshState,
                    isRefreshing = uiState.isRefreshing,
                    onRefresh = { screenModel.syncWithServer(isPullToRefresh = true) },
                    modifier = Modifier.fillMaxSize(),
                    indicator = {
                        PullToRefreshDefaults.Indicator(
                            state = pullToRefreshState,
                            isRefreshing = uiState.isRefreshing,
                            modifier = Modifier.align(Alignment.TopCenter),
                            containerColor = customColors.surfaceVariant,
                            color = customColors.gourmetOrange
                        )
                    }
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        val isError = !uiState.errorMessage.isNullOrBlank()
                        val isEmpty = uiState.items.isEmpty()

                        when {
                            uiState.isLoading && isEmpty && !isError -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = customColors.gourmetOrange)
                                }
                            }

                            isError && isEmpty -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    FilledTonalButton(
                                        onClick = { screenModel.syncWithServer(isPullToRefresh = false) },
                                        enabled = !uiState.isLoading,
                                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                                        colors = ButtonDefaults.filledTonalButtonColors(
                                            containerColor = customColors.surfaceVariant,
                                            contentColor = customColors.textPrimary
                                        )
                                    ) {
                                        AnimatedContent(
                                            targetState = uiState.isLoading,
                                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                                            label = "ButtonLoadingTransition"
                                        ) { loading ->
                                            if (loading) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(20.dp),
                                                    strokeWidth = 2.dp,
                                                    color = customColors.textPrimary
                                                )
                                            } else {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = Icons.Default.Refresh,
                                                        contentDescription = stringResource(Res.string.active_devices_retry),
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = stringResource(Res.string.active_devices_retry),
                                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // 3. BOŞ LİSTE DURUMU
                            isEmpty -> {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Place,
                                        contentDescription = null,
                                        modifier = Modifier.size(64.dp),
                                        tint = customColors.placeHolderIcon.copy(alpha = 0.5f)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = stringResource(Res.string.visit_history_empty),
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            color = customColors.textSecondary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }

                            // 4. LİSTE VE SAYFALANDIRMA DURUMU
                            else -> {
                                LazyColumn(
                                    state = listState,
                                    modifier = Modifier
                                        .widthIn(max = 800.dp)
                                        .fillMaxSize(),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(
                                        items = uiState.items,
                                        key = { it.visitId }
                                    ) { visit ->
                                        val deleteTitle = stringResource(Res.string.visit_item_delete_desc)
                                        val deleteMessage = stringResource(Res.string.dialog_delete_visit_message, visit.placeName ?: "")
                                        VisitItemCard(
                                            visit = visit,
                                            onItemClick = {
                                                // navigator.push(PlaceDetailScreen(placeId = visit.placeId))
                                            },
                                            onDeleteClick = {
                                                activeDialog = DialogConfig(
                                                    title = deleteTitle,
                                                    message = deleteMessage,
                                                    confirmText = "Sil",
                                                    isDestructive = true,
                                                    onConfirm = { screenModel.deleteVisit(visit.visitId) }
                                                )
                                            }
                                        )
                                    }

                                    // SAYFA ALTI YÜKLEME VEYA TEKRAR DENE FOOTER'I
                                    if (!uiState.isLastPage) {
                                        if (uiState.isLoadingMore) {
                                            item {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 16.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    CircularProgressIndicator(
                                                        modifier = Modifier.size(24.dp),
                                                        color = customColors.gourmetOrange,
                                                        strokeWidth = 2.5.dp
                                                    )
                                                }
                                            }
                                        } else if (
                                            uiState.isLoadingMoreError) {
                                            item {
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 12.dp),
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    IconButton(
                                                        onClick = {
                                                            screenModel.clearMessages()
                                                            screenModel.loadMore()
                                                        }
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Refresh,
                                                            contentDescription = stringResource(Res.string.active_devices_retry),
                                                            tint = customColors.textSecondary,
                                                            modifier = Modifier.size(28.dp)
                                                        )
                                                    }
                                                    Text(
                                                        text = stringResource(Res.string.active_devices_retry),
                                                        style = MaterialTheme.typography.bodySmall.copy(
                                                            color = customColors.textSecondary,
                                                            fontWeight = FontWeight.Medium
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        activeDialog?.let { config ->
            TastyConfirmDialog(
                config = config,
                onDismiss = { activeDialog = null }
            )
        }
    }
}

@Composable
private fun VisitItemCard(
    visit: VisitResponse,
    onItemClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val customColors = LocalCustomColors.current
    var isExpanded by remember { mutableStateOf(false) }

    val locationText = listOfNotNull(
        visit.neighbourhood?.takeIf { it.isNotBlank() },
        visit.district?.takeIf { it.isNotBlank() },
        visit.city?.takeIf { it.isNotBlank() }
    ).joinToString(", ")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { isExpanded = !isExpanded }
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ),
        colors = CardDefaults.cardColors(containerColor = customColors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(customColors.wave),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = customColors.navy,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = visit.placeName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = customColors.textPrimary
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        if (visit.averagePoint > 0.0) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .background(
                                        color = customColors.gourmetOrange,
                                        shape = RoundedCornerShape(percent = 50)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = stringResource(Res.string.visit_item_rating_desc),
                                    tint = Color.White,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = visit.averagePoint.toString(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = visit.categories ?: stringResource(Res.string.visit_item_category_unspecified),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = customColors.textSecondary.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Medium
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(Res.string.visit_item_delete_desc),
                            tint = customColors.textSecondary.copy(alpha = 0.4f),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = customColors.textSecondary.copy(alpha = 0.5f),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(
                    color = customColors.textSecondary.copy(alpha = 0.12f),
                    thickness = 1.dp
                )
                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Tam Adres Satırı
                    if (locationText.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Place,
                                contentDescription = null,
                                tint = customColors.textSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = locationText,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = customColors.textPrimary
                                )
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarToday,
                            contentDescription = null,
                            tint = customColors.textSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = formatToRelativeDateTime(visit.createdAt),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = customColors.textSecondary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        FilledTonalButton(
                            onClick = onItemClick,
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = customColors.gourmetOrange.copy(alpha = 0.12f),
                                contentColor = customColors.gourmetOrange
                            )
                        ) {
                            Text(
                                text = "Mekana Git",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
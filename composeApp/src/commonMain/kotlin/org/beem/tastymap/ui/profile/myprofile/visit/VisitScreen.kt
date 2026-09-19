package org.beem.tastymap.ui.profile.myprofile.visit

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
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
import tastymap.composeapp.generated.resources.dialog_block_message
import tastymap.composeapp.generated.resources.dialog_block_title
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
                !uiState.isLoading && totalItems > 0 && lastVisibleItem >= totalItems - 2

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
                                fontWeight = FontWeight.Bold
                            )
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = customColors.background
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
                            containerColor = customColors.placeHolderBack,
                            color = customColors.placeHolderIcon
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
                                        modifier = Modifier.height(48.dp),
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
                                                    modifier = Modifier.size(24.dp),
                                                    strokeWidth = 2.5.dp,
                                                    color = customColors.textPrimary
                                                )
                                            } else {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = Icons.Default.Refresh,
                                                        contentDescription = stringResource(Res.string.active_devices_retry),
                                                        modifier = Modifier.size(24.dp),
                                                        tint = customColors.textPrimary
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = stringResource(Res.string.active_devices_retry),
                                                        style = MaterialTheme.typography.titleSmall.copy(
                                                            fontWeight = FontWeight.Bold,
                                                            color = customColors.textPrimary
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            isEmpty -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stringResource(Res.string.visit_history_empty),
                                        style = MaterialTheme.typography.bodyMedium.copy(color = customColors.textSecondary)
                                    )
                                }
                            }

                            else -> {
                                LazyColumn(
                                    state = listState,
                                    modifier = Modifier
                                        .widthIn(max = 1500.dp)
                                        .fillMaxSize(),
                                    contentPadding = PaddingValues(bottom = 16.dp, top = 8.dp)
                                ) {
                                    items(
                                        items = uiState.items,
                                        key = { it.visitId }
                                    ) { visit ->
                                        val deleteTitle = stringResource(Res.string.visit_item_delete_desc)
                                        val deleteMessage = stringResource(Res.string.dialog_delete_visit_message,visit.placeName ?: "")
                                        VisitItemCard(
                                            visit = visit,
                                            onItemClick = {
                                                // İsteğe bağlı: Mekan detayına git
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

                                    // Sayfalama Loading Göstergesi (Listenin en altında)
                                    if (uiState.isLoadingMore) {
                                        item {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(24.dp),
                                                    color = customColors.gourmetOrange
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

    val locationText = listOfNotNull(
        visit.neighbourhood?.takeIf { it.isNotBlank() },
        visit.district?.takeIf { it.isNotBlank() },
        visit.city?.takeIf { it.isNotBlank() }
    ).joinToString(", ")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 3.dp)
            .clickable { onItemClick() },
        colors = CardDefaults.cardColors(containerColor = customColors.surfaceVariant.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sol Taraf: Modern Mekan İkonu
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(customColors.gourmetOrange.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = customColors.gourmetOrange,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Orta Kısım: Mekan Bilgileri
            Column(modifier = Modifier.weight(1f)) {
                // 1. Satır: Mekan Adı ve Puan
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

                    // Puan 0'dan büyükse göster
                    if (visit.averagePoint > 0.0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(customColors.gourmetOrange.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = stringResource(Res.string.visit_item_rating_desc),
                                tint = customColors.gourmetOrange,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = visit.averagePoint.toString(),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = customColors.gourmetOrange
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                if (locationText.isNotEmpty()) {
                    Text(
                        text = locationText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = customColors.textSecondary
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // 3. Satır: Kategori ve Tarih
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = visit.categories ?: stringResource(Res.string.visit_item_category_unspecified),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = customColors.textSecondary.copy(alpha = 0.8f)
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = formatToRelativeDateTime(visit.createdAt),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = customColors.textSecondary.copy(alpha = 0.6f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription =stringResource(Res.string.visit_item_delete_desc),
                    tint = customColors.textSecondary.copy(alpha = 0.5f)
                )
            }
        }
    }
}
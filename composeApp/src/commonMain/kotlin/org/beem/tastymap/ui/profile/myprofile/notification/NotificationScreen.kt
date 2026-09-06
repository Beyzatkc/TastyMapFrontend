package org.beem.tastymap.ui.profile.myprofile.notification

import TastyButton
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import org.beem.tastymap.core.util.ToastManager
import org.beem.tastymap.data.model.socialnotifications.NotificationActionStatus
import org.beem.tastymap.data.model.socialnotifications.SocialNotificationType
import org.beem.tastymap.data.model.socialnotifications.SocialNotificationsResponse
import org.beem.tastymap.domain.model.RelationStatus
import org.beem.tastymap.ui.profile.otherprofile.ProfileScreen
import org.beem.tastymap.ui.theme.LocalCustomColors

class NotificationScreen() : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<NotificationScreenModel>()
        val uiState by screenModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val customColors = LocalCustomColors.current

        val pullToRefreshState = rememberPullToRefreshState()
        val listState = rememberLazyListState()

        LaunchedEffect(Unit) {
            screenModel.loadInitialData()
            screenModel.markAllAsRead()
        }

        LaunchedEffect(uiState.errorMessage) {
            uiState.errorMessage?.let { message ->
                ToastManager.show(message)
            }
        }

        // Pagination Trigger
        val shouldLoadMore = remember {
            derivedStateOf {
                val totalItems = listState.layoutInfo.totalItemsCount
                val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                totalItems > 0 && lastVisibleItem >= totalItems - 2
            }
        }

        LaunchedEffect(shouldLoadMore.value) {
            if (shouldLoadMore.value) {
                screenModel.loadNextPage()
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Geri",
                                tint = customColors.textPrimary
                            )
                        }
                    },
                    title = {
                        Text(
                            text = "Bildirimler",
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
                    onRefresh = { screenModel.refresh() },
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
                            // 1. İlk Yükleme
                            uiState.isLoading && isEmpty && !isError -> {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = customColors.gourmetOrange)
                                }
                            }

                            // 2. Hata ve Liste Boşsa (Tekrar Dene)
                            isError && isEmpty -> {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    FilledTonalButton(
                                        onClick = { screenModel.loadInitialData() },
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
                                            label = "RetryButton"
                                        ) { loading ->
                                            if (loading) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(24.dp), strokeWidth = 2.5.dp, color = customColors.textPrimary
                                                )
                                            } else {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(24.dp))
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text("Tekrar Dene", fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            isEmpty -> {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("Henüz hiç bildirimin yok.", color = customColors.textSecondary)
                                }
                            }

                            else -> {
                                LazyColumn(
                                    state = listState,
                                    modifier = Modifier
                                        .widthIn(max = 1500.dp)
                                        .fillMaxSize(),
                                    contentPadding = PaddingValues(bottom = 16.dp)
                                ) {
                                    items(items = uiState.items, key = { it.id }) { notification ->
                                        NotificationItem(
                                            notification = notification,
                                            onItemClick = {
                                                navigator.push(ProfileScreen(userId = notification.actor.id))
                                            },
                                            onAccept = {
                                                screenModel.acceptRequest(notification.id, notification.actor.id)
                                            },
                                            onReject = {
                                                screenModel.rejectRequest(notification.id, notification.actor.id)
                                            },
                                            onToggleFollow = { status ->
                                                screenModel.toggleFollow(notification.id, notification.actor.id, status)
                                            }
                                        )
                                    }

                                    if (uiState.isLoadingMore) {
                                        item {
                                            Box(
                                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = customColors.gourmetOrange)
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
}

@Composable
private fun NotificationItem(
    notification: SocialNotificationsResponse,
    onItemClick: () -> Unit,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onToggleFollow: (RelationStatus) -> Unit
) {
    val customColors = LocalCustomColors.current

    // Okunmamış bildirimlere hafif bir arkaplan rengi veriyoruz
    val itemBackgroundColor = if (notification.isRead) {
        Color.Transparent
    } else {
        customColors.surfaceVariant.copy(alpha = 0.4f)
    }

    val notificationMessage = getNotificationMessage(notification.type)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(itemBackgroundColor)
            .clickable { onItemClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // --- Profil Fotoğrafı ---
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(customColors.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (!notification.actor.profilePhotoUrl.isNullOrBlank()) {
                AsyncImage(
                    model = notification.actor.profilePhotoUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = customColors.placeHolderIcon,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // --- Bildirim Metni ---
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = notification.actor.username,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = customColors.textPrimary
                )
            )
            Text(
                text = notificationMessage,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = customColors.textSecondary
                )
            )

            // Eğer PENDING (Bekleyen İstek) durumundaysa Butonları Alt Satırda Göster
            if (notification.actionStatus == NotificationActionStatus.PENDING) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TastyButton(
                        text = "Onayla",
                        onClick = onAccept,
                        modifier = Modifier.weight(1f),
                        isPrimary = true,
                        backcolor = customColors.gourmetOrange,
                        textcolor = Color.White,
                        strokecolor = Color.Transparent
                    )
                    TastyButton(
                        text = "Sil",
                        onClick = onReject,
                        modifier = Modifier.weight(1f),
                        isPrimary = false,
                        backcolor = customColors.surfaceVariant,
                        textcolor = customColors.textPrimary,
                        strokecolor = Color.Transparent
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // --- Sağ Taraftaki Dinamik Buton veya Metin ---
        when (notification.actionStatus) {
            NotificationActionStatus.ACCEPTED -> {
                // İstek onaylandıysa RelationStatus'a göre dinamik takip butonu
                notification.actor.relationStatus?.let { relationStatus ->
                    val style = getActionStyleForRelation(relationStatus, customColors)

                    if (style != null) {
                        TastyButton(
                            text = style.text,
                            onClick = { onToggleFollow(relationStatus) },
                            modifier = Modifier.width(110.dp),
                            isPrimary = style.isPrimary,
                            backcolor = style.backColor,
                            textcolor = style.textColor,
                            strokecolor = style.strokeColor
                        )
                    }
                }
            }
            NotificationActionStatus.REJECTED -> {
                Text(
                    text = "Reddedildi",
                    style = MaterialTheme.typography.labelSmall.copy(color = customColors.textSecondary)
                )
            }
            NotificationActionStatus.PENDING -> {
                // Butonları zaten metnin altına (Column içine) çizdik. Burası boş kalabilir.
            }
            NotificationActionStatus.NONE -> {
                // Beğeni/Yorum gibi etkileşimsiz bildirimler. Gerekirse gönderi fotoğrafı (TargetDTO) çizilebilir.
            }
        }
    }
}

// --- Yardımcı Fonksiyonlar ---

@Composable
private fun getNotificationMessage(type: SocialNotificationType): String {
    return when (type) {
        SocialNotificationType.FOLLOW_REQUEST -> "seni takip etmek istiyor."
        SocialNotificationType.FOLLOW_ACCEPTED -> "takip isteğini kabul etti."
        SocialNotificationType.NEW_FOLLOWER -> "seni takip etmeye başladı."
        SocialNotificationType.POST_LIKE -> "bir gönderini beğendi."
        SocialNotificationType.COMMENT -> "gönderine yorum yaptı."
    }
}

@Composable
private fun getActionStyleForRelation(
    relationStatus: RelationStatus,
    customColors: org.beem.tastymap.ui.theme.CustomColors
): ActionStyle? {
    return when (relationStatus) {
        RelationStatus.FOLLOWING -> ActionStyle(
            text = "Takip Ediliyor",
            backColor = Color.Transparent,
            textColor = customColors.textPrimary,
            strokeColor = customColors.textSecondary.copy(alpha = 0.4f),
            isPrimary = false
        )
        RelationStatus.PENDING -> ActionStyle(
            text = "İstek Gitti",
            backColor = Color.Transparent,
            textColor = customColors.textSecondary,
            strokeColor = customColors.textSecondary.copy(alpha = 0.3f),
            isPrimary = false
        )
        RelationStatus.FOLLOW_BACK -> ActionStyle(
            text = "Sende Takip Et",
            backColor = customColors.gourmetOrange,
            textColor = Color.White,
            strokeColor = Color.Transparent,
            isPrimary = true
        )
        RelationStatus.NOT_FOLLOWING -> ActionStyle(
            text = "Takip Et",
            backColor = customColors.gourmetOrange,
            textColor = Color.White,
            strokeColor = Color.Transparent,
            isPrimary = true
        )
        RelationStatus.SELF -> null
    }
}

private data class ActionStyle(
    val text: String,
    val backColor: Color,
    val textColor: Color,
    val strokeColor: Color,
    val isPrimary: Boolean
)
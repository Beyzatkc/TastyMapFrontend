package org.beem.tastymap.ui.profile.otherprofile

import TastyButton
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import org.beem.tastymap.domain.model.RelationStatus
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.profile_action_accept
import tastymap.composeapp.generated.resources.profile_action_reject
import tastymap.composeapp.generated.resources.profile_back_cd
import tastymap.composeapp.generated.resources.profile_blocked_message
import tastymap.composeapp.generated.resources.profile_default_bio
import tastymap.composeapp.generated.resources.profile_default_name
import tastymap.composeapp.generated.resources.profile_default_photo_cd
import tastymap.composeapp.generated.resources.profile_follow_back
import tastymap.composeapp.generated.resources.profile_incoming_request_message
import tastymap.composeapp.generated.resources.profile_map_empty
import tastymap.composeapp.generated.resources.profile_metric_following
import tastymap.composeapp.generated.resources.profile_metric_posts
import tastymap.composeapp.generated.resources.profile_metric_subscribers
import tastymap.composeapp.generated.resources.profile_pending
import tastymap.composeapp.generated.resources.profile_photo_cd
import tastymap.composeapp.generated.resources.profile_posts_empty
import tastymap.composeapp.generated.resources.profile_subscribe
import tastymap.composeapp.generated.resources.profile_subscribed
import tastymap.composeapp.generated.resources.profile_tab_posts
import tastymap.composeapp.generated.resources.profile_tab_taste_map

class ProfileScreen(private val userId: Long) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<ProfileScreenModel>()
        val state by screenModel.profileState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        val pullToRefreshState = rememberPullToRefreshState()
        val customColors = LocalCustomColors.current
        var selectedTab by remember { mutableIntStateOf(0) }

        LaunchedEffect(userId) {
            screenModel.getProfile(userId)
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(Res.string.profile_back_cd),
                                tint = Color.White
                            )
                        }
                    },
                    title = {
                        Text(
                            text = "@${state.profile?.username ?: ""}",
                            style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
                        )
                    },
                    actions = {
                        IconButton(onClick = { /* Menü/Engelle İşlemleri */ }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = customColors.darkHeaderColor
                    )
                )
            }
        ) { innerPadding ->
            PullToRefreshBox(
                state = pullToRefreshState,
                isRefreshing = state.isRefreshing,
                onRefresh = { screenModel.getProfile(userId, true) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(customColors.placeHolderBack),
                indicator = {
                    PullToRefreshDefaults.Indicator(
                        state = pullToRefreshState,
                        isRefreshing = state.isRefreshing,
                        modifier = Modifier.align(Alignment.TopCenter),
                        containerColor = customColors.placeHolderBack,
                        color = customColors.placeHolderIcon
                    )
                }
            ) {
                if (state.isLoading && state.profile == null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = customColors.gourmetOrange)
                    }
                } else {
                    val profile = state.profile
                    val isBlocked = profile?.blockedByMe == true || profile?.blockedMe == true

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 1. Üst Kart Bilgileri
                        item {
                            Surface(
                                color = customColors.darkHeaderColor,
                                shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .widthIn(max = 600.dp)
                                            .padding(horizontal = 20.dp, vertical = 20.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        // Profil Fotoğrafı
                                        Box(contentAlignment = Alignment.BottomEnd) {
                                            Box(
                                                modifier = Modifier
                                                    .size(92.dp)
                                                    .clip(CircleShape)
                                                    .border(3.dp, customColors.gourmetOrange, CircleShape)
                                                    .background(customColors.placeHolderBack),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (!profile?.profilePhoto.isNullOrBlank() && !isBlocked) {
                                                    AsyncImage(
                                                        model = profile?.profilePhoto,
                                                        contentDescription = stringResource(Res.string.profile_photo_cd),
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .clip(CircleShape),
                                                        contentScale = ContentScale.Crop
                                                    )
                                                } else {
                                                    Icon(
                                                        imageVector = Icons.Default.Person,
                                                        contentDescription = stringResource(Res.string.profile_default_photo_cd),
                                                        tint = customColors.placeHolderIcon,
                                                        modifier = Modifier.size(48.dp)
                                                    )
                                                }
                                            }

                                            profile?.role?.let { role ->
                                                Surface(
                                                    color = customColors.gourmetOrange,
                                                    shape = RoundedCornerShape(6.dp),
                                                    modifier = Modifier.offset(y = 4.dp)
                                                ) {
                                                    Text(
                                                        text = role,
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                                        style = MaterialTheme.typography.labelSmall.copy(
                                                            color = Color.White,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Text(
                                            text = profile?.let { "${it.name} ${it.surname}".trim() }
                                                .takeIf { !it.isNullOrBlank() }
                                                ?: stringResource(Res.string.profile_default_name),
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontSize = 18.sp,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = if (!isBlocked) {
                                                profile?.biography ?: stringResource(Res.string.profile_default_bio)
                                            } else {
                                                ""
                                            },
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = Color.White.copy(alpha = 0.8f),
                                                textAlign = TextAlign.Center
                                            ),
                                            modifier = Modifier.padding(horizontal = 16.dp)
                                        )

                                        Spacer(modifier = Modifier.height(20.dp))

                                        if (profile?.hasPendingIncomingRequest == true) {
                                            IncomingRequestCard(
                                                username = profile.username,
                                                isLoading = state.isActionLoading,
                                                onAcceptClick = {
                                                    screenModel.acceptRequest(userId)
                                                },
                                                onRejectClick = {
                                                    screenModel.rejectRequest(userId)
                                                }
                                            )
                                        }
                                        ProfileActionButton(
                                            relationStatus = profile?.relationStatus ?: RelationStatus.NOT_FOLLOWING,
                                            isBlocked = isBlocked,
                                            isLoading = state.isActionLoading,
                                            onActionClick = { status ->
                                                screenModel.handleFollowAction(userId, status)
                                            }
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // 2. Metrik Kartlar (Engelli Değilse)
                        if (!isBlocked) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        modifier = Modifier.widthIn(max = 600.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        MetricCard(
                                            title = stringResource(Res.string.profile_metric_posts),
                                            value = (profile?.postCount ?: 0).toString(),
                                            modifier = Modifier.weight(1f),
                                            cardColor = customColors.surfaceVariant
                                        )
                                        MetricCard(
                                            title = stringResource(Res.string.profile_metric_subscribers),
                                            value = (profile?.subscriberCount ?: 0).toString(),
                                            modifier = Modifier.weight(1f),
                                            cardColor = customColors.surfaceVariant
                                        )
                                        MetricCard(
                                            title = stringResource(Res.string.profile_metric_following),
                                            value = (profile?.subscribedCount ?: 0).toString(),
                                            modifier = Modifier.weight(1f),
                                            cardColor = customColors.surfaceVariant
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            // 3. Sekmeler
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Surface(
                                        color = customColors.surfaceVariant,
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.widthIn(max = 600.dp)
                                    ) {
                                        Row(modifier = Modifier.padding(4.dp)) {
                                            TabButton(
                                                text = stringResource(Res.string.profile_tab_posts),
                                                icon = Icons.Default.GridOn,
                                                isSelected = selectedTab == 0,
                                                onClick = { selectedTab = 0 },
                                                modifier = Modifier.weight(1f),
                                                activeColor = customColors.navy,
                                                accentColor = customColors.gourmetOrange
                                            )
                                            TabButton(
                                                text = stringResource(Res.string.profile_tab_taste_map),
                                                icon = Icons.Default.Map,
                                                isSelected = selectedTab == 1,
                                                onClick = { selectedTab = 1 },
                                                modifier = Modifier.weight(1f),
                                                activeColor = customColors.navy,
                                                accentColor = customColors.gourmetOrange
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            // 4. Boş İçerik Metinleri
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (selectedTab == 0) {
                                            stringResource(Res.string.profile_posts_empty)
                                        } else {
                                            stringResource(Res.string.profile_map_empty)
                                        },
                                        style = MaterialTheme.typography.bodyMedium.copy(color = customColors.textSecondary),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        } else {
                            // Engellenmiş Durum Mesajı
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = customColors.textSecondary,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = stringResource(Res.string.profile_blocked_message),
                                        style = MaterialTheme.typography.bodyMedium.copy(color = customColors.textSecondary),
                                        textAlign = TextAlign.Center
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

@Composable
private fun ProfileActionButton(
    relationStatus: RelationStatus,
    isBlocked: Boolean,
    isLoading: Boolean,
    onActionClick: (RelationStatus) -> Unit
) {
    val customColors = LocalCustomColors.current

    if (isBlocked || relationStatus == RelationStatus.SELF) return

    val (buttonText, backColor, textColor, strokeColor) = when (relationStatus) {
        RelationStatus.FOLLOWING -> Tuple4(
            stringResource(Res.string.profile_subscribed),
            Color.Transparent,
            Color.White,
            Color.White.copy(alpha = 0.6f)
        )
        RelationStatus.PENDING -> Tuple4(
            stringResource(Res.string.profile_pending),
            Color.Transparent,
            Color.White.copy(alpha = 0.8f),
            Color.White.copy(alpha = 0.4f)
        )
        RelationStatus.FOLLOW_BACK -> Tuple4(
            stringResource(Res.string.profile_follow_back),
            customColors.gourmetOrange,
            Color.White,
            Color.Transparent
        )
        RelationStatus.NOT_FOLLOWING -> Tuple4(
            stringResource(Res.string.profile_subscribe),
            customColors.gourmetOrange,
            Color.White,
            Color.Transparent
        )
        RelationStatus.SELF -> Tuple4("", Color.Transparent, Color.Transparent, Color.Transparent)
    }

    TastyButton(
        text = buttonText,
        onClick = { onActionClick(relationStatus) },
        modifier = Modifier.fillMaxWidth(),
        isPrimary = true,
        isLoading = isLoading,
        backcolor = backColor,
        textcolor = textColor,
        strokecolor = strokeColor
    )
}

private data class Tuple4<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

@Composable
private fun MetricCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    cardColor: Color
) {
    val customColors = LocalCustomColors.current

    Surface(
        color = cardColor,
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(color = customColors.textPrimary)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                color = customColors.textSecondary,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun TabButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    activeColor: Color,
    accentColor: Color
) {
    val customColors = LocalCustomColors.current

    Surface(
        color = if (isSelected) activeColor else Color.Transparent,
        shape = RoundedCornerShape(10.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = if (isSelected) accentColor else customColors.textSecondary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                style = if (isSelected) MaterialTheme.typography.titleMedium.copy(color = customColors.surface)
                else MaterialTheme.typography.bodyMedium.copy(color = customColors.textSecondary)
            )
        }
    }
}
@Composable
private fun IncomingRequestCard(
    username: String,
    isLoading: Boolean,
    onAcceptClick: () -> Unit,
    onRejectClick: () -> Unit
) {
    val customColors = LocalCustomColors.current

    Surface(
        color = customColors.surfaceVariant,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(Res.string.profile_incoming_request_message, username),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = customColors.textPrimary,
                    fontWeight = FontWeight.SemiBold
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = customColors.gourmetOrange
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Onayla Butonu
                    TastyButton(
                        text = stringResource(Res.string.profile_action_accept),
                        onClick = onAcceptClick,
                        modifier = Modifier.weight(1f),
                        isPrimary = true,
                        backcolor = customColors.gourmetOrange,
                        textcolor = Color.White
                    )

                    // Sil / Reddet Butonu
                    TastyButton(
                        text = stringResource(Res.string.profile_action_reject),
                        onClick = onRejectClick,
                        modifier = Modifier.weight(1f),
                        isPrimary = false,
                        backcolor = Color.Transparent,
                        textcolor = customColors.textSecondary,
                        strokecolor = customColors.textSecondary.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}
package org.beem.tastymap.ui.profile.myprofile

import TastyButton
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
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
import org.beem.tastymap.core.util.ToastManager
import org.beem.tastymap.ui.profile.myprofile.editprofile.EditProfileScreen
import org.beem.tastymap.ui.profile.myprofile.notification.NotificationScreen
import org.beem.tastymap.ui.profile.myprofile.settings.SettingsScreen
import org.beem.tastymap.ui.profile.otherprofile.ProfileScreen
import org.beem.tastymap.ui.profile.subscribers.SubscriberListType
import org.beem.tastymap.ui.profile.subscribers.SubscribersListScreen
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.my_profile_default_bio
import tastymap.composeapp.generated.resources.my_profile_default_name
import tastymap.composeapp.generated.resources.my_profile_default_photo_cd
import tastymap.composeapp.generated.resources.my_profile_edit_button
import tastymap.composeapp.generated.resources.my_profile_map_empty
import tastymap.composeapp.generated.resources.my_profile_metric_following
import tastymap.composeapp.generated.resources.my_profile_metric_posts
import tastymap.composeapp.generated.resources.my_profile_metric_subscribers
import tastymap.composeapp.generated.resources.my_profile_notification_cd
import tastymap.composeapp.generated.resources.my_profile_photo_cd
import tastymap.composeapp.generated.resources.my_profile_posts_empty
import tastymap.composeapp.generated.resources.my_profile_settings_cd
import tastymap.composeapp.generated.resources.my_profile_tab_map
import tastymap.composeapp.generated.resources.my_profile_tab_posts

class MyProfileScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<MyProfileScreenModel>()
        val state by screenModel.myProfileState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        val pullToRefreshState = rememberPullToRefreshState()
        val customColors = LocalCustomColors.current
        var selectedTab by remember { mutableIntStateOf(0) }

        LaunchedEffect(Unit) {
            screenModel.getMyProfile()
        }

        LaunchedEffect(state.successMessageRes, state.errorMessage) {
            state.successMessageRes?.let { res ->
                ToastManager.show(getString(res))
                screenModel.clearMessagesProfile()
            }
            state.errorMessage?.let { message ->
                ToastManager.show(message)
                screenModel.clearMessagesProfile()
            }
        }

        var showTestUserSheet by remember { mutableStateOf(false) }

        if (showTestUserSheet) {
            ModalBottomSheet(
                onDismissRequest = { showTestUserSheet = false },
                containerColor = customColors.background
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Test: Tüm Kullanıcılar (${state.DENEME.size})",
                        style = MaterialTheme.typography.titleMedium,
                        color = customColors.textPrimary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    if (state.isLoading) {
                        CircularProgressIndicator(
                            color = customColors.gourmetOrange,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxHeight(0.6f)) {
                            items(state.DENEME) { user ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            showTestUserSheet = false
                                            navigator.push(ProfileScreen(userId = user.id))
                                        }
                                        .padding(vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "@${user.username}",
                                            fontWeight = FontWeight.Bold,
                                            color = customColors.textPrimary
                                        )
                                        Text(
                                            text = "ID: ${user.id} | ${user.name ?: ""} ${user.surname ?: ""}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = customColors.textSecondary
                                        )
                                    }
                                }
                                HorizontalDivider(color = customColors.surfaceVariant)
                            }
                        }
                    }
                }
            }
        }
        // --- GECICI TEST ALANI BİTİŞİ ---

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "@" + state.profile?.username,
                            style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
                        )
                    },
                    actions = {
                        // --- GECICI TEST BUTONU ---
                        TextButton(onClick = {
                            screenModel.getAllUsers()
                            showTestUserSheet = true
                        }) {
                            Text("TEST USERS", color = customColors.gourmetOrange, fontWeight = FontWeight.Bold)
                        }
                        // --------------------------
                        IconButton(onClick = { navigator.push(NotificationScreen()) }) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = stringResource(Res.string.my_profile_notification_cd),
                                tint = Color.White
                            )
                        }

                        IconButton(onClick = { navigator.push(SettingsScreen()) }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = stringResource(Res.string.my_profile_settings_cd),
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
                onRefresh = { screenModel.refreshProfile() },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(customColors.placeHolderBack),
                indicator = {
                    PullToRefreshDefaults.Indicator(
                        state = pullToRefreshState,
                        isRefreshing = state.isRefreshing,
                        modifier = Modifier.align(Alignment.TopCenter),
                        containerColor =customColors.placeHolderBack,
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
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 1. Lacivert Üst Alan (Ekranı tam kaplar, içeriği ortalar)
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
                                        Box(contentAlignment = Alignment.BottomEnd) {
                                            Box(
                                                modifier = Modifier
                                                    .size(92.dp)
                                                    .clip(CircleShape)
                                                    .border(3.dp, customColors.gourmetOrange, CircleShape)
                                                    .background(customColors.placeHolderBack),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (!state.profile?.profilePhoto.isNullOrBlank()) {
                                                    AsyncImage(
                                                        model = state.profile?.profilePhoto,
                                                        contentDescription = stringResource(Res.string.my_profile_photo_cd),
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .clip(CircleShape),
                                                        contentScale = ContentScale.Crop
                                                    )
                                                } else {
                                                    Icon(
                                                        imageVector = Icons.Default.Person,
                                                        contentDescription = stringResource(Res.string.my_profile_default_photo_cd),
                                                        tint = customColors.placeHolderIcon,
                                                        modifier = Modifier.size(48.dp)
                                                    )
                                                }
                                            }

                                            state.profile?.role?.let { role ->
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
                                            text = state.profile?.name ?: stringResource(Res.string.my_profile_default_name),
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontSize = 18.sp,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = state.profile?.biography ?: stringResource(Res.string.my_profile_default_bio),
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = Color.White.copy(alpha = 0.8f),
                                                textAlign = TextAlign.Center
                                            ),
                                            modifier = Modifier.padding(horizontal = 16.dp)
                                        )

                                        Spacer(modifier = Modifier.height(20.dp))

                                        TastyButton(
                                            text = stringResource(Res.string.my_profile_edit_button),
                                            onClick = { navigator.push(EditProfileScreen())},
                                            modifier = Modifier.fillMaxWidth(),
                                            isPrimary = true,
                                            isLoading = state.isActionLoading,
                                            backcolor = customColors.gourmetOrange,
                                            textcolor = Color.White,
                                            strokecolor = Color.Transparent
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // 2. Metrik Kartlar (Max 480dp genişlikle ortalanır)
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
                                        title = stringResource(Res.string.my_profile_metric_posts),
                                        onClick = {
                                        },
                                        value = (state.profile?.postCount ?: 0).toString(),
                                        modifier = Modifier.weight(1f),
                                        cardColor = customColors.surfaceVariant
                                    )
                                    MetricCard(
                                        title = stringResource(Res.string.my_profile_metric_subscribers),
                                        value = (state.profile?.subscriberCount ?: 0).toString(),
                                        onClick = {
                                            state.profile?.userId?.let { userId ->
                                                navigator.push(
                                                    SubscribersListScreen(
                                                        userId = userId,
                                                        initialTab = SubscriberListType.SUBSCRIBERS
                                                    )
                                                )
                                            }
                                        },
                                        modifier = Modifier.weight(1f),
                                        cardColor = customColors.surfaceVariant
                                    )
                                    MetricCard(
                                        title = stringResource(Res.string.my_profile_metric_following),
                                        value = (state.profile?.subscribedCount ?: 0).toString(),
                                        onClick = {
                                            state.profile?.userId?.let { userId ->
                                                navigator.push(
                                                    SubscribersListScreen(
                                                        userId = userId,
                                                        initialTab = SubscriberListType.SUBSCRIBES
                                                    )
                                                )
                                            }
                                        },
                                        modifier = Modifier.weight(1f),
                                        cardColor = customColors.surfaceVariant
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // 3. Sekmeler (Max 480dp genişlikle ortalanır)
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
                                            text = stringResource(Res.string.my_profile_tab_posts),
                                            icon = Icons.Default.GridOn,
                                            isSelected = selectedTab == 0,
                                            onClick = { selectedTab = 0 },
                                            modifier = Modifier.weight(1f),
                                            activeColor = customColors.navy,
                                            accentColor = customColors.gourmetOrange
                                        )
                                        TabButton(
                                            text = stringResource(Res.string.my_profile_tab_map),
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

                        // 4. İçerik Yazısı (Max 480dp genişlikle ortalanır)
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier.widthIn(max = 600.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (selectedTab == 0) {
                                        Text(
                                            text = stringResource(Res.string.my_profile_posts_empty),
                                            style = MaterialTheme.typography.bodyMedium.copy(color = customColors.textSecondary),
                                            textAlign = TextAlign.Center
                                        )
                                    } else {
                                        Text(
                                            text = stringResource(Res.string.my_profile_map_empty),
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
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cardColor: Color
) {
    val customColors = LocalCustomColors.current

    Surface(
        onClick = onClick,
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
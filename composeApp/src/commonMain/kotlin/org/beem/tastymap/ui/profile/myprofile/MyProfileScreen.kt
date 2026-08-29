package org.beem.tastymap.ui.profile.myprofile

import TastyButton
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
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
import org.beem.tastymap.ui.profile.myprofile.settings.SettingsScreen
import org.beem.tastymap.ui.theme.LocalCustomColors

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

        LaunchedEffect(state.successMessage, state.errorMessage) {
            state.successMessage?.let { message ->
                screenModel.clearMessages()
            }
            state.errorMessage?.let { message ->
                ToastManager.show(message)
                screenModel.clearMessages()
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Profilim",
                            // customColors.surface yerine Color.White verildi (her iki modda da okunabilir)
                            style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
                        )
                    },
                    actions = {
                        IconButton(onClick = { navigator.push(SettingsScreen()) }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Ayarlar",
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
                onRefresh = { screenModel.getMyProfile(true) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                indicator = {
                    PullToRefreshDefaults.Indicator(
                        state = pullToRefreshState,
                        isRefreshing = state.isRefreshing,
                        modifier = Modifier.align(Alignment.TopCenter),
                        containerColor = customColors.background,
                        color = customColors.darkHeaderColor
                    )
                }
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (state.isLoading && state.profile == null) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = customColors.gourmetOrange)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(customColors.background)
                        ) {
                            item {
                                Surface(
                                    color = customColors.darkHeaderColor,
                                    shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
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
                                                        contentDescription = "Profil Fotoğrafı",
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .clip(CircleShape),
                                                        contentScale = ContentScale.Crop
                                                    )
                                                } else {
                                                    Icon(
                                                        imageVector = Icons.Default.Person,
                                                        contentDescription = "Varsayılan Profil",
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

                                        // Kullanıcı Adı
                                        Text(
                                            text = state.profile?.name ?: "Benim Adım",
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontSize = 18.sp,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        // Biyografi
                                        Text(
                                            text = state.profile?.biography ?: "Henüz bir biyografi eklemediniz.",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = Color.White.copy(alpha = 0.8f),
                                                textAlign = TextAlign.Center
                                            ),
                                            modifier = Modifier.padding(horizontal = 16.dp)
                                        )

                                        Spacer(modifier = Modifier.height(20.dp))

                                        TastyButton(
                                            text = "Profili Düzenle",
                                            onClick = { /* Profil Düzenleme Aç */ },
                                            modifier = Modifier.fillMaxWidth(),
                                            isPrimary = true,
                                            isLoading = state.isActionLoading,
                                            backcolor = customColors.gourmetOrange,
                                            textcolor = Color.White,
                                            strokecolor = Color.Transparent
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }


                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    MetricCard(
                                        title = "Paylaşımım",
                                        value = (state.profile?.postCount ?: 0).toString(),
                                        modifier = Modifier.weight(1f),
                                        cardColor = customColors.surfaceVariant
                                    )
                                    MetricCard(
                                        title = "Takipçim",
                                        value = (state.profile?.subscriberCount ?: 0).toString(),
                                        modifier = Modifier.weight(1f),
                                        cardColor = customColors.surfaceVariant
                                    )
                                    MetricCard(
                                        title = "Takibim",
                                        value = (state.profile?.subscribedCount ?: 0).toString(),
                                        modifier = Modifier.weight(1f),
                                        cardColor = customColors.surfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            item {
                                Surface(
                                    color = customColors.surfaceVariant,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp)
                                ) {
                                    Row(modifier = Modifier.padding(4.dp)) {
                                        TabButton(
                                            text = "Paylaşımlarım",
                                            icon = Icons.Default.GridOn,
                                            isSelected = selectedTab == 0,
                                            onClick = { selectedTab = 0 },
                                            modifier = Modifier.weight(1f),
                                            activeColor = customColors.navy,
                                            accentColor = customColors.gourmetOrange
                                        )
                                        TabButton(
                                            text = "Haritam",
                                            icon = Icons.Default.Map,
                                            isSelected = selectedTab == 1,
                                            onClick = { selectedTab = 1 },
                                            modifier = Modifier.weight(1f),
                                            activeColor = customColors.navy,
                                            accentColor = customColors.gourmetOrange
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (selectedTab == 0) {
                                        Text(
                                            text = "Paylaştığınız tüm lezzet incelemeleri burada listelenecek.",
                                            style = MaterialTheme.typography.bodyMedium.copy(color = customColors.textSecondary)
                                        )
                                    } else {
                                        Text(
                                            text = "Kaydettiğiniz ve işaretlediğiniz kişisel lezzet haritanız.",
                                            style = MaterialTheme.typography.bodyMedium.copy(color = customColors.textSecondary)
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
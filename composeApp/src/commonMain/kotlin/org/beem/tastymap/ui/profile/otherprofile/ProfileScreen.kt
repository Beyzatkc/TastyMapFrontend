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
import androidx.compose.material.icons.filled.Map
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import org.beem.tastymap.domain.model.UserProfile
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.beem.tastymap.ui.theme.TastyTheme

class ProfileScreen(private val userId: Long) : Screen {

    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<ProfileScreenModel>()
        val state by screenModel.profileState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(userId) {
            screenModel.getProfile(userId)
        }

        ProfileContent(
            profile = state.profile,
            isLoading = state.isLoading,
            isRefreshing = state.isRefreshing,
            onRefresh = { screenModel.getProfile(userId, isFromPullToRefresh = true) },
            onBackClick = { navigator.pop() },
            onSubscribeClick = { /* Abone Ol / Çık işlemi */ },
            onShareClick = { /* Profili Paylaş */ }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    profile: UserProfile?,
    isLoading: Boolean = false,
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onSubscribeClick: () -> Unit = {},
    onShareClick: () -> Unit = {}
) {
    val customColors = LocalCustomColors.current
    val pullToRefreshState = rememberPullToRefreshState()
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = customColors.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = profile?.username?.let { "@$it" } ?: "Gurme Profili",
                        style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
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
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(customColors.placeHolderBack),
            indicator = {
                PullToRefreshDefaults.Indicator(
                    state = pullToRefreshState,
                    isRefreshing = isRefreshing,
                    modifier = Modifier.align(Alignment.TopCenter),
                    containerColor = customColors.placeHolderBack,
                    color = customColors.placeHolderIcon
                )
            }
        ) {
            if (isLoading && profile == null) {
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
                    // 1. HERO BAŞLIK KARTI (Lacivert alan tam genişlik kaplar, içerik ortalanır)
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
                                        .widthIn(max = 480.dp)
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
                                            if (!profile?.profilePhoto.isNullOrBlank()) {
                                                AsyncImage(
                                                    model = profile.profilePhoto,
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
                                        text = profile?.name ?: "Kullanıcı",
                                        style = MaterialTheme.typography.headlineMedium.copy(color = Color.White)
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = profile?.biography ?: "Henüz bir lezzet biyografisi eklenmedi.",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = Color.White.copy(alpha = 0.8f),
                                            textAlign = TextAlign.Center
                                        ),
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )

                                    Spacer(modifier = Modifier.height(20.dp))

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(IntrinsicSize.Min),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        var isSubscribed by remember { mutableStateOf(false) }

                                        TastyButton(
                                            text = if (isSubscribed) "Abonesin" else "Abone Ol",
                                            onClick = {
                                                isSubscribed = !isSubscribed
                                                onSubscribeClick()
                                            },
                                            modifier = Modifier.weight(1f),
                                            isPrimary = true,
                                            isLoading = isLoading,
                                            backcolor = if (isSubscribed) customColors.textSecondary else customColors.gourmetOrange,
                                            textcolor = Color.White,
                                            strokecolor = Color.Transparent
                                        )

                                        TastyButton(
                                            text = "Paylaş",
                                            onClick = onShareClick,
                                            modifier = Modifier.weight(1f),
                                            isPrimary = false,
                                            isLoading = false,
                                            backcolor = Color.Transparent,
                                            textcolor = Color.White,
                                            strokecolor = Color.White
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // 2. METRİK KARTLARI (Max 480dp ile ortalanır)
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier.widthIn(max = 480.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                MetricCard(
                                    title = "Paylaşım",
                                    value = (profile?.postCount ?: 0).toString(),
                                    modifier = Modifier.weight(1f),
                                    cardColor = customColors.surfaceVariant
                                )
                                MetricCard(
                                    title = "Abone",
                                    value = (profile?.subscriberCount ?: 0).toString(),
                                    modifier = Modifier.weight(1f),
                                    cardColor = customColors.surfaceVariant
                                )
                                MetricCard(
                                    title = "Takip",
                                    value = (profile?.subscribedCount ?: 0).toString(),
                                    modifier = Modifier.weight(1f),
                                    cardColor = customColors.surfaceVariant
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // 3. TAB SEÇİM ALANI (Max 480dp ile ortalanır)
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
                                modifier = Modifier.widthIn(max = 480.dp)
                            ) {
                                Row(modifier = Modifier.padding(4.dp)) {
                                    TabButton(
                                        text = "Paylaşımlar",
                                        icon = Icons.Default.GridOn,
                                        isSelected = selectedTab == 0,
                                        onClick = { selectedTab = 0 },
                                        modifier = Modifier.weight(1f),
                                        activeColor = customColors.navy,
                                        accentColor = customColors.gourmetOrange
                                    )
                                    TabButton(
                                        text = "Lezzet Haritası",
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

                    // 4. İÇERİK ALANI (Max 480dp ile ortalanır)
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier.widthIn(max = 480.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (selectedTab == 0) {
                                    Text(
                                        text = "Gurmenin Son İncelemeleri ve Fotoğrafları",
                                        style = MaterialTheme.typography.bodyMedium.copy(color = customColors.textSecondary),
                                        textAlign = TextAlign.Center
                                    )
                                } else {
                                    Text(
                                        text = "Gurmenin İşaretlediği Mekanlar ve İğneler (Map View)",
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
                style = if (isSelected) MaterialTheme.typography.titleMedium.copy(color = Color.White)
                else MaterialTheme.typography.bodyMedium.copy(color = customColors.textSecondary)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    TastyTheme {
        ProfileContent(
            profile = UserProfile(
                userId = 101L,
                username = "gurme_ahmet",
                name = "Ahmet Yılmaz",
                profilePhoto = null,
                role = "GURME",
                biography = "İstanbul lezzet haritasını çıkaran sokak gurmesi 🍕🍔",
                postCount = 42,
                subscriberCount = 1250,
                subscribedCount = 380,
                blockedByMe = false,
                blockedMe = false
            )
        )
    }
}
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
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
            screenModel.getProfile2(userId)
        }

        ProfileContent(
            profile = state.profile,
            isLoading = state.isLoading,
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
    onBackClick: () -> Unit = {},
    onSubscribeClick: () -> Unit = {},
    onShareClick: () -> Unit = {}
) {
    val customColors = LocalCustomColors.current
    val pageBackgroundColor = Color(0xFFFAFAF8)
    val darkHeaderColor = Color(0xFF18345A)
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = pageBackgroundColor,
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
                    containerColor = darkHeaderColor
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(pageBackgroundColor)
                .padding(innerPadding)
        ) {
            // 1. HERO BAŞLIK KARTI
            item {
                Surface(
                    color = darkHeaderColor,
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
                                    .background(Color.DarkGray)
                            )

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
                            color = Color.White,
                            style = MaterialTheme.typography.headlineMedium.copy(color = Color.White)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = profile?.biography ?: "Henüz bir lezzet biyografisi eklenmedi.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White.copy(alpha = 0.9f),
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
                                backcolor = if (isSubscribed) Color.Gray else customColors.gourmetOrange,
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
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 2. METRİK KARTLARI
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Paylaşım",
                        value = (profile?.postCount ?: 0).toString(),
                        modifier = Modifier.weight(1f),
                        cardColor = Color.LightGray.copy(alpha = 0.2f)
                    )
                    MetricCard(
                        title = "Abone",
                        value = (profile?.subscriberCount ?: 0).toString(),
                        modifier = Modifier.weight(1f),
                        cardColor = Color.LightGray.copy(alpha = 0.2f)
                    )
                    MetricCard(
                        title = "Takip",
                        value = (profile?.subscribedCount ?: 0).toString(),
                        modifier = Modifier.weight(1f),
                        cardColor = Color.LightGray.copy(alpha = 0.2f)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 3. TAB SEÇİM ALANI
            item {
                Surface(
                    color = Color.LightGray.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
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
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 4. İÇERİK ALANI
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedTab == 0) {
                        Text(
                            text = "Gurmenin Son İncelemeleri ve Fotoğrafları",
                            style = MaterialTheme.typography.bodyMedium.copy(color = customColors.textSecondary)
                        )
                    } else {
                        Text(
                            text = "Gurmenin İşaretlediği Mekanlar ve İğneler (Map View)",
                            style = MaterialTheme.typography.bodyMedium.copy(color = customColors.textSecondary)
                        )
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
                style = MaterialTheme.typography.headlineMedium.copy(color = Color.Black)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                color = Color.DarkGray,
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
                tint = if (isSelected) accentColor else Color.DarkGray,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                style = if (isSelected) MaterialTheme.typography.titleMedium.copy(color = Color.White)
                else MaterialTheme.typography.bodyMedium.copy(color = Color.DarkGray)
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


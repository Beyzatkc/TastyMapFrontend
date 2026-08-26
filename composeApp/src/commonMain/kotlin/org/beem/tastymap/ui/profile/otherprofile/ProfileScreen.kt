package org.beem.tastymap.ui.profile.otherprofile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
            onSendMessageClick = { /* Mesaj Gönder */ }
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
    onSendMessageClick: () -> Unit = {}
) {
    val customColors = LocalCustomColors.current
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = profile?.username?.let { "@$it" } ?: "Gurme Profili",
                        style = MaterialTheme.typography.titleMedium.copy(color = customColors.navy)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = customColors.navy
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor =customColors.wave.copy(alpha = 0.6f)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // 1. HERO BAŞLIK KARTI (Ferah Pastel Mavi / Soft Zemin)
            Surface(
                color = customColors.wave.copy(alpha = 0.6f), // Boğucu lacivert yerine yumuşak pastel zemin
                shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profil Fotoğrafı ve Rol Rozeti
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Box(
                            modifier = Modifier
                                .size(92.dp)
                                .clip(CircleShape)
                                .border(3.dp, customColors.gourmetOrange, CircleShape) // Turuncu halka ile canlılık
                                .background(customColors.gray)
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

                    // İsim ve Biyografi (Lacivert sadece metinde kontrast için kullanıldı)
                    Text(
                        text = profile?.name ?: "Kullanıcı",
                        style = MaterialTheme.typography.headlineMedium.copy(color = customColors.navy)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = profile?.biography ?: "Henüz bir lezzet biyografisi eklenmedi.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = customColors.textSecondary
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // ETKİLEŞİM BUTONLARI (Turuncu & Lacivert Çerçeveli Soft Tasarım)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        var isSubscribed by remember { mutableStateOf(false) }

                        Button(
                            onClick = {
                                isSubscribed = !isSubscribed
                                onSubscribeClick()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSubscribed) customColors.gray else customColors.gourmetOrange,
                                contentColor = if (isSubscribed) customColors.navy else Color.White
                            )
                        ) {
                            Text(
                                text = if (isSubscribed) "Abonesin" else "Abone Ol",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        OutlinedButton(
                            onClick = onSendMessageClick,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(customColors.navy)
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = customColors.navy)
                        ) {
                            Text(
                                text = "Mesaj Gönder",
                                style = MaterialTheme.typography.titleMedium.copy(color = customColors.navy)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. METRİK KARTLARI (Beyaz Zemin Üzerine Hafif Gölge / Soft Çerçeve)
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
                    navyColor = customColors.navy,
                    cardColor = customColors.surfaceVariant
                )
                MetricCard(
                    title = "Abone",
                    value = (profile?.subscriberCount ?: 0).toString(),
                    modifier = Modifier.weight(1f),
                    navyColor = customColors.navy,
                    cardColor = customColors.surfaceVariant
                )
                MetricCard(
                    title = "Takip",
                    value = (profile?.subscribedCount ?: 0).toString(),
                    modifier = Modifier.weight(1f),
                    navyColor = customColors.navy,
                    cardColor = customColors.surfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. TAB SEÇİM ALANI
            Surface(
                color = customColors.surfaceVariant,
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

            // 4. İÇERİK ALANI
            Box(
                modifier = Modifier
                    .fillMaxSize()
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

@Composable
private fun MetricCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    navyColor: Color,
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
                style = MaterialTheme.typography.headlineMedium.copy(color = navyColor)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
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
                tint = if (isSelected) accentColor else Color.Gray,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                style = if (isSelected) MaterialTheme.typography.titleMedium.copy(color = Color.White)
                else MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
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
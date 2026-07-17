import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.beem.tastymap.ui.theme.CustomColors
import org.beem.tastymap.ui.theme.LocalCustomColors

@Composable
fun WelcomeScreen(
    onStart: () -> Unit = {},
    onSkip: () -> Unit = {}
) {
    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        showContent = true
    }

    val customColors = LocalCustomColors.current
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center // Webde tüm arayüzü ekranın tam ortasına toplar
    ) {
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(animationSpec = tween(800)) +
                    slideInVertically(
                        initialOffsetY = { it / 10 },
                        animationSpec = tween(800, easing = EaseOutCubic)
                    )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp)
                    // WEBİ TOPARLAYAN DOKUNUŞ: Arayüzün webde bir şerit gibi yayılmasını engeller, kompakt bir mobil penceresi gibi tutar
                    .widthIn(max = 440.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                // Üst Başlık ve Logo Alanı
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    WelcomeLogo(customColors = customColors)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Yapay zekamız size en doğru restoran ve beslenme önerilerini sunabilmek için sizi tanımak istiyor.",
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Center,
                        color = customColors.navy.copy(alpha = 0.75f),
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                WelcomeCardContent(
                    customColors = customColors,
                    onStart = onStart,
                    onSkip = onSkip
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun WelcomeLogo(customColors: CustomColors) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "HOŞ GELDİNİZ",
            color = customColors.navy.copy(alpha = 0.35f),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 3.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "TastyMap",
                color = customColors.navy,
                fontWeight = FontWeight.Black,
                fontSize = 40.sp,
                letterSpacing = (-1).sp
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .background(
                        color = customColors.yellow,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "AI",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            color = customColors.yellow.copy(alpha = 0.12f),
            shape = RoundedCornerShape(50.dp),
            border = BorderStroke(1.2.dp, customColors.yellow.copy(alpha = 0.25f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = customColors.yellow,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Kişisel lezzet asistanınız",
                    color = customColors.navy,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun WelcomeCardContent(
    customColors: CustomColors,
    onStart: () -> Unit,
    onSkip: () -> Unit
) {
    Card(
        modifier = Modifier
            .widthIn(max = 500.dp)
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(32.dp),
                clip = false,
                ambientColor = customColors.navy.copy(alpha = 0.12f),
                spotColor = customColors.navy.copy(alpha = 0.22f)
            ),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Sütunlar artık kart daraldığı için webde de birbirine yakın ve derli toplu duracak
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FeatureColumn(
                    customColors = customColors,
                    icon = Icons.Rounded.Psychology,
                    title = "Akıllı",
                    description = "Zevklerinizi\nöğrenir",
                    modifier = Modifier.weight(1f)
                )
                FeatureColumn(
                    customColors = customColors,
                    icon = Icons.Rounded.HealthAndSafety,
                    title = "Sağlıklı",
                    description = "Size özel\nfiltreler",
                    modifier = Modifier.weight(1f)
                )
                FeatureColumn(
                    customColors = customColors,
                    icon = Icons.Rounded.Restaurant,
                    title = "Özgün",
                    description = "Restoran\nönerileri",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Buton genişliği kart genişliğiyle (max 440dp) uyumlu olarak sınırlandırıldı, yayılma bitti
            Button(
                onClick = onStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(18.dp),
                        clip = false,
                        spotColor = customColors.navy.copy(alpha = 0.35f)
                    ),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = customColors.navy
                ),
                contentPadding = PaddingValues(horizontal = 24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Başlayalım",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Rounded.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = onSkip,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = "Şimdilik Atla",
                    color = customColors.navy.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
fun FeatureColumn(
    customColors: CustomColors,
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = customColors.navy.copy(alpha = 0.06f),
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = customColors.navy,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = customColors.navy,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = description,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = customColors.navy.copy(alpha = 0.88f),
            textAlign = TextAlign.Center,
            lineHeight = 17.sp
        )
    }
}
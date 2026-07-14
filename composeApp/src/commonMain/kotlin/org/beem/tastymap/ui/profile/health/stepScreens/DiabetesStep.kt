package org.beem.tastymap.ui.profile.health.stepScreens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.beem.tastymap.ui.profile.health.HealthUiState
import org.jetbrains.compose.resources.painterResource

@Composable
fun DiabetesStep(
    state: HealthUiState,
    onDiabetesChanged: (Boolean) -> Unit
) {
    // Web ve tabletlerde taşma olmaması için dikey kaydırma desteği ekledik
    val scrollState = rememberScrollState()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        // Ekran genişliğine göre maksimum genişlik belirliyoruz (Web/Tablet uyumu için)
        val isWideScreen = maxWidth > 600.dp
        val contentWidth = if (isWideScreen) 500.dp else Modifier.fillMaxWidth()

        Column(
            modifier = Modifier
                .widthIn(max = 550.dp) // Webde içeriğin çok yayılmasını engeller
                .then(if (isWideScreen) Modifier.padding(40.dp) else Modifier.padding(horizontal = 24.dp))
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(140.dp)
            ) {
                // Nefes alma animasyonu için ölçeklendirme (scale) değerini tanımlıyoruz
                val infiniteTransition = rememberInfiniteTransition(label = "breathing")
                val pulseScale by infiniteTransition.animateFloat(
                    initialValue = 0.95f, // En küçük boyutu
                    targetValue = 1.08f,  // En büyük boyutu
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 1500), // 1.5 saniyede bir nefes alıp verecek
                        repeatMode = RepeatMode.Reverse // Geriye doğru pürüzsüzce küçülecek
                    ),
                    label = "pulseScale"
                )

                // Arkadaki eğlenceli dekoratif halka (Bu da emojiyle birlikte hafifçe büyür)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = pulseScale
                            scaleY = pulseScale
                        }
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                            shape = CircleShape
                        )
                )
                // İçteki daha koyu halka (Sabit durarak derinlik algısı yaratır)
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                            shape = CircleShape
                        )
                )
                // Ana Karakter/Görsel (Emoji) - Animasyonla nefes alıyor!
                Image(
                    painter = painterResource(id = R.drawable.ic_diabetes_illustration), // Kendi görselinin adı
                    contentDescription = "Diyabet Görseli",
                    modifier = Modifier
                        .size(80.dp) // Görselin boyutu
                        .graphicsLayer {
                            scaleX = pulseScale
                            scaleY = pulseScale
                        }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- 2. Soru ve Açıklama Metni ---
            Text(
                text = "Diyabet Durumunuz Nedir?",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold, // Daha dinamik ve canlı durması için Bold -> ExtraBold yapıldı
                    color = MaterialTheme.colorScheme.onBackground
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Sihirli mutfağımızın size en sağlıklı ve lezzetli tarifleri önerebilmesi için bu minik bilgiye ihtiyacımız var! ✨",
                style = MaterialTheme.typography.bodyMedium.copy(
                    lineHeight = 22.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // --- 3. Seçim Kartları (Web & Mobil Uyumlu Grid/Row) ---
            // Geniş ekranda yan yana, çok dar ekranda alt alta gelmesi için esnek Row yapılandırması
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // EVET KARTI
                SelectionCard(
                    text = "Evet, diyabetim var",
                    iconText = "🩺", // Daha medikal/eğlenceli bir emoji
                    isSelected = state.hasDiabetes,
                    onClick = { onDiabetesChanged(true) },
                    modifier = Modifier.weight(1f)
                )

                // HAYIR KARTI
                SelectionCard(
                    text = "Hayır, şeker gibi sağlamım", // Daha esprili bir dil
                    iconText = "💪",
                    isSelected = !state.hasDiabetes,
                    onClick = { onDiabetesChanged(false) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SelectionCard(
    text: String,
    iconText: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // --- Renk Animasyonları ---
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        animationSpec = tween(durationMillis = 200),
        label = "bg"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
        animationSpec = tween(durationMillis = 200),
        label = "border"
    )

    val borderWidth by animateDpAsState(
        targetValue = if (isSelected) 2.5.dp else 1.dp,
        animationSpec = tween(durationMillis = 200),
        label = "borderWidth"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
        label = "text"
    )

    // --- Eğlenceli Zıplama (Pop/Offset) Efekti ---
    // Kart seçildiğinde hafifçe yukarı kalkar (-6dp) ve boyutu biraz büyür
    val yOffset by animateDpAsState(
        targetValue = if (isSelected) (-6).dp else 0.dp,
        animationSpec = tween(durationMillis = 200),
        label = "offset"
    )

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.03f else 1.0f,
        animationSpec = tween(durationMillis = 200),
        label = "scale"
    )

    Box(
        modifier = modifier
            .height(150.dp)
            .offset(y = yOffset)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(RoundedCornerShape(28.dp)) // Daha da yuvarlatılmış modern köşeler
            .border(
                border = BorderStroke(borderWidth, borderColor),
                shape = RoundedCornerShape(28.dp)
            )
            .background(backgroundColor)
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = androidx.compose.material3.ripple()
            )
            .padding(16.dp)
    ) {
        // Sağ üst köşe tık işareti
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Seçildi",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.TopEnd)
            )
        }

        // Kart İçeriği
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = iconText,
                fontSize = 38.sp, // Emojiyi biraz daha büyüterek odağı artırdık
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    letterSpacing = 0.3.sp
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}
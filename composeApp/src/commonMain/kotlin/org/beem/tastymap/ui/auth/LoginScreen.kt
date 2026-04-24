package org.beem.tastymap.ui.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import org.beem.tastymap.ui.components.TastyTextField

class LoginScreen : Screen {
    @Composable
    override fun Content() {
        var isLoginTab by remember { mutableStateOf(true) }

        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedBackground()

            Column(
                modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(top = 50.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Tasty Map",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Lezzeti Keşfet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                )
            }

            Box(
                modifier = Modifier.fillMaxSize().navigationBarsPadding(),
                contentAlignment = Alignment.BottomCenter // Kutuyu aşağıya yakın konumlandıralım
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.95f) // Biraz daha geniş tutalım
                        .widthIn(max = 500.dp)
                        .padding(bottom = 40.dp) // Alttan boşluk
                        .animateContentSize(),
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)), // Hafif şeffaf
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Sekme Seçici (Senin kodun, stilize edilmiş)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(50.dp))
                                .padding(4.dp)
                        ) {
                            TabButton("Giriş Yap", isLoginTab, Modifier.weight(1f)) { isLoginTab = true }
                            TabButton("Kayıt Ol", !isLoginTab, Modifier.weight(1f)) { isLoginTab = false }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // FORM ALANLARI
                        if (isLoginTab) {
                            LoginForm()
                        } else {
                            RegisterForm()
                        }
                    }
                }
            }
        }
    }
}

// --- YARDIMCI COMPOSABLE'LAR ---

@Composable
fun TabButton(text: String, isSelected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    // Seçili sekmeyi belli eden animasyonlu arka plan
    val backgroundColor by animateColorAsState(
        if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    )
    val textColor by animateColorAsState(
        if (isSelected) Color.White else MaterialTheme.colorScheme.primary
    )

    Surface(
        modifier = modifier.fillMaxHeight(),
        color = backgroundColor,
        shape = RoundedCornerShape(50.dp),
        onClick = onClick
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = text, color = textColor, fontWeight = FontWeight.SemiBold)
        }
    }
}


@Composable
fun AnimatedBackground() {
    // Senin keskin lacivert tonun ve açık tonu
    val navy = MaterialTheme.colorScheme.primary
    val lightNavy = navy.copy(alpha = 0.1f)

    // SÜREKLİ YÜZEN (FLOATING) ANİMASYON
    val infiniteTransition = rememberInfiniteTransition()
    val dy by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 20f, // 20px yukarı aşağı yüzecek
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFFDFDFD))) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. Arka Plandaki Kıvrımlı Çizgi (Bezier Curve)
            val path = Path().apply {
                moveTo(0f, height * 0.3f)
                cubicTo(
                    width * 0.3f, height * 0.2f, // Kontrol noktası 1
                    width * 0.7f, height * 0.4f, // Kontrol noktası 2
                    width, height * 0.3f         // Bitiş noktası
                )
                // Çizgiyi aşağıya tamamlayıp içini boyayalım (Opsiyonel)
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }

            // Hafif bir gradyanla içini boyayalım
            drawPath(
                path = path,
                brush = Brush.verticalGradient(
                    colors = listOf(lightNavy, Color.White),
                    startY = height * 0.3f
                )
            )

            // Eğrinin kendisini çizelim
            drawPath(
                path = path,
                color = navy.copy(alpha = 0.3f),
                style = Stroke(width = 3.dp.toPx())
            )
        }

        // 2. YÜZEN MARKERLAR (İkonlar)
        // İkonları koordinatlara göre yerleştirip animasyonlu 'dy' (float) değerini ekliyoruz.
        FloatingIcon(Icons.Default.Restaurant, navy, 0.2f, 0.25f, dy) // Çatal Bıçak
        FloatingIcon(Icons.Default.Restaurant, navy, 0.80f, 0.80f, dy)
        FloatingIcon(Icons.Default.LocationOn, navy, 0.50f, 0.35f, dy * 1.2f) // Marker 1
        FloatingIcon(Icons.Default.Place, navy, 0.8f, 0.28f, dy * 0.8f) // Marker 2
    }
}

@Composable
fun BoxScope.FloatingIcon(icon: ImageVector, color: Color, xPercent: Float, yPercent: Float, dy: Float) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = Modifier
            .align(Alignment.TopStart)
            .offset(
                x = (Modifier.fillMaxSize().run { xPercent } * 100).dp, // Basit hesaplama
                y = (Modifier.fillMaxSize().run { yPercent } * 100).dp + dy.dp // Animasyonlu dy
            )
            .size(32.dp),
        tint = color.copy(alpha = 0.6f)
    )
}

// --- FORM TASLAKLARI (Şimdilik Boş) ---
@Composable fun LoginForm() { /* TextField'lar ve Buton */ }
@Composable fun RegisterForm() { /* TextField'lar ve Buton */ }
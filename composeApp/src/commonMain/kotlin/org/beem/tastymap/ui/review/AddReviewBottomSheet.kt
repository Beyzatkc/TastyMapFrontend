package org.beem.tastymap.ui.review

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.beem.tastymap.review.AddReviewEvent
import org.beem.tastymap.review.AddReviewScreenModel
import org.beem.tastymap.review.model.ScoreType
import org.beem.tastymap.ui.review.components.TastyRatingBar
import org.beem.tastymap.ui.review.components.TastyScoreSlider
import org.beem.tastymap.ui.theme.AppColors
import org.beem.tastymap.ui.theme.getAppFontFamily
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReviewBottomSheet(
    placeId: String,
    restaurantName: String,
    initialMainScore: Double,
    onDismiss: () -> Unit,
    onReviewSubmittedSuccessfully: () -> Unit,
    screenModel: AddReviewScreenModel = koinInject()
) {
    val state by screenModel.state.collectAsState()
    val fontFamily = getAppFontFamily()

    LaunchedEffect(Unit) {
        screenModel.initInitialScore(initialMainScore)
        screenModel.event.collect { event ->
            when (event) {
                is AddReviewEvent.Success -> onReviewSubmittedSuccessfully()
                is AddReviewEvent.Error -> { /* Snackbar/Toast gösterilebilir */ }
            }
        }
    }

    val primaryCriteria = remember {
        listOf(ScoreType.TASTE, ScoreType.SERVICE, ScoreType.PRICE_PERFORMANCE)
    }
    val secondaryCriteria = remember {
        ScoreType.entries.filter { it !in primaryCriteria }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = AppColors.Surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = AppColors.BorderStrong.copy(alpha = 0.5f))
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Mekan Başlığı
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = restaurantName,
                            fontFamily = fontFamily,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextPrimary
                        )
                        Text(
                            text = "Deneyimini puanla ve değerlendir",
                            fontFamily = fontFamily,
                            fontSize = 13.sp,
                            color = AppColors.TextTertiary
                        )
                    }
                }

                // 1. Hero Pin Puanlama Barı
                item {
                    TastyRatingBar(
                        rating = state.mainScore,
                        onRatingChange = screenModel::onMainScoreChange
                    )
                }

                // 2. Temel Kriterler
                item {
                    Text(
                        text = "Detaylı Kriterler (İsteğe Bağlı)",
                        fontFamily = fontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                items(primaryCriteria) { type ->
                    TastyScoreSlider(
                        title = type.displayName,
                        score = state.subScores[type] ?: 0.0,
                        onScoreChange = { screenModel.onSubScoreChange(type, it) }
                    )
                }

                // 3. İkincil Kriterler (Accordion)
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { screenModel.toggleAdvancedCriteria() }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (state.isAdvancedExpanded) "Daha az kriter göster" else "Diğer kriterleri ekle (Temizlik, Hız vb.)",
                            fontFamily = fontFamily,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.NavySoft
                        )
                        Icon(
                            imageVector = if (state.isAdvancedExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = AppColors.NavySoft,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                if (state.isAdvancedExpanded) {
                    items(secondaryCriteria) { type ->
                        TastyScoreSlider(
                            title = type.displayName,
                            score = state.subScores[type] ?: 0.0,
                            onScoreChange = { screenModel.onSubScoreChange(type, it) }
                        )
                    }
                }

                // 4. Yorum Metin Alanı
                item {
                    OutlinedTextField(
                        value = state.comment,
                        onValueChange = screenModel::onCommentChange,
                        placeholder = {
                            Text(
                                "Mekan hakkındaki diğer notlarını yaz...",
                                fontFamily = fontFamily,
                                fontSize = 13.sp,
                                color = AppColors.TextTertiary
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = AppColors.SurfaceVariant,
                            unfocusedContainerColor = AppColors.SurfaceVariant,
                            focusedBorderColor = AppColors.GourmetOrange,
                            unfocusedBorderColor = AppColors.BorderLight,
                            cursorColor = AppColors.GourmetOrange
                        )
                    )
                }
            }

            // 5. Sticky Gönder Butonu
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = AppColors.Surface,
                shadowElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Button(
                        onClick = { screenModel.submitReview(placeId) },
                        enabled = !state.isSubmitting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppColors.GourmetOrange,
                            disabledContainerColor = AppColors.GourmetOrange.copy(alpha = 0.5f)
                        )
                    ) {
                        if (state.isSubmitting) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(
                                text = "Değerlendirmeyi Tamamla",
                                fontFamily = fontFamily,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
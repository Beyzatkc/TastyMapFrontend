package org.beem.tastymap.ui.review

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.beem.tastymap.place.model.review.ReviewItem
import org.beem.tastymap.place.model.review.UserReviewSummaryDto
import org.beem.tastymap.review.AddReviewEvent
import org.beem.tastymap.review.AddReviewScreenModel
import org.beem.tastymap.review.model.ScoreType
import org.beem.tastymap.ui.components.dialog.TastyConfirmationDialog
import org.beem.tastymap.ui.components.dialog.TastyDialogType
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
    initialMainScore: Double = 0.0,
    existingReview: UserReviewSummaryDto? = null,
    onDismiss: () -> Unit,
    onReviewSubmittedSuccessfully: (review: ReviewItem, userSummary: UserReviewSummaryDto) -> Unit,
    onReviewUpdatedSuccessfully: (review: ReviewItem, userSummary: UserReviewSummaryDto) -> Unit,
    onReviewDeletedSuccessfully: (reviewId: Long, score: Double) -> Unit,
    screenModel: AddReviewScreenModel = koinInject()
) {
    val state by screenModel.state.collectAsState()
    val fontFamily = getAppFontFamily()
    val isEditMode = existingReview != null

    LaunchedEffect(existingReview) {
        screenModel.setupReviewForm(existingReview, initialMainScore)
    }

    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(screenModel) {
        screenModel.event.collect { event ->
            when (event) {
                is AddReviewEvent.Created -> {
                    onReviewSubmittedSuccessfully(event.review, event.userSummary)
                }
                is AddReviewEvent.Updated -> {
                    onReviewUpdatedSuccessfully(event.review, event.userSummary)
                }
                is AddReviewEvent.Deleted -> {
                    onReviewDeletedSuccessfully(event.reviewId, event.deletedScore)
                }
                is AddReviewEvent.Error -> {
                    println("TastyMap UI -> Hata Eventi: ${event.message}")
                }
            }
        }
    }

    val primaryCriteria = remember {
        listOf(ScoreType.TASTE, ScoreType.SERVICE, ScoreType.PRICE_PERFORMANCE)
    }
    val secondaryCriteria = remember {
        ScoreType.entries.filter { it !in primaryCriteria && it != ScoreType.OVERALL }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = AppColors.Surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = AppColors.BorderStrong.copy(alpha = 0.4f))
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Başlık Alanı
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = if (isEditMode) "Değerlendirmeni Düzenle" else restaurantName,
                            fontFamily = fontFamily,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextPrimary
                        )
                        Text(
                            text = if (isEditMode) "$restaurantName için deneyimini güncelle veya sil" else "Deneyimini puanla ve değerlendir",
                            fontFamily = fontFamily,
                            fontSize = 13.sp,
                            color = AppColors.TextSecondary
                        )
                    }
                }

                // 1. Ana Yıldız Puanı Barı
                item {
                    Surface(
                        color = AppColors.SurfaceVariant,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 16.dp, horizontal = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            TastyRatingBar(
                                rating = state.mainScore,
                                onRatingChange = screenModel::onMainScoreChange
                            )
                        }
                    }
                }

                // 2. Temel Kriterler Başlığı
                item {
                    Text(
                        text = "Detaylı Kriterler (İsteğe Bağlı)",
                        fontFamily = fontFamily,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary,
                        modifier = Modifier.padding(top = 2.dp)
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
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AppColors.SurfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                screenModel.toggleAdvancedCriteria()
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
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
                                imageVector = if (state.isAdvancedExpanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                                contentDescription = null,
                                tint = AppColors.NavySoft,
                                modifier = Modifier.size(20.dp)
                            )
                        }
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
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences
                        ),
                        placeholder = {
                            Text(
                                "Mekan hakkındaki deneyimini ve gurme notlarını paylaş...",
                                fontFamily = fontFamily,
                                fontSize = 13.sp,
                                color = AppColors.TextTertiary
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(84.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = AppColors.Surface,
                            unfocusedContainerColor = AppColors.Surface,
                            focusedBorderColor = AppColors.GourmetOrange,
                            unfocusedBorderColor = AppColors.BorderLight,
                            cursorColor = AppColors.GourmetOrange
                        )
                    )
                }
            }

            // 5. Alt Buton Barı (Edit Modu / Yeni Kayıt Modu)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = AppColors.Surface,
                border = BorderStroke(1.dp, AppColors.BorderLight)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (isEditMode) {
                        OutlinedButton(
                            onClick = { showDeleteDialog = true },
                            enabled = !state.isDeleting && !state.isSubmitting,
                            modifier = Modifier
                                .weight(0.32f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = AppColors.ErrorRed
                            ),
                            border = BorderStroke(1.dp, AppColors.ErrorRed.copy(alpha = 0.4f))
                        ) {
                            if (state.isDeleting) {
                                CircularProgressIndicator(
                                    color = AppColors.ErrorRed,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Text(
                                    text = "Sil",
                                    fontFamily = fontFamily,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppColors.ErrorRed
                                )
                            }
                        }
                        if (showDeleteDialog) {
                            TastyConfirmationDialog(
                                title = "Yorumu Sil?",
                                message = "Bu mekana yaptığın değerlendirme ve verdiğin puanlar kalıcı olarak silinecektir.",
                                confirmText = "Sil",
                                dismissText = "Vazgeç",
                                type = TastyDialogType.DANGER,
                                isLoading = state.isDeleting,
                                onConfirm = {
                                    screenModel.deleteReview(placeId)
                                    showDeleteDialog = false
                                },
                                onDismiss = { showDeleteDialog = false }
                            )
                        }
                    }

                    Button(
                        onClick = {
                            if (isEditMode) screenModel.updateReview(placeId)
                            else screenModel.submitReview(placeId)
                        },
                        enabled = !state.isSubmitting && !state.isDeleting,
                        modifier = Modifier
                            .weight(if (isEditMode) 0.68f else 1f)
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
                                text = if (isEditMode) "Güncelle" else "Değerlendirmeyi Tamamla",
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
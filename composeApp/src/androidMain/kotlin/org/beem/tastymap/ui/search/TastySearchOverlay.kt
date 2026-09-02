package org.beem.tastymap.ui.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.beem.tastymap.search.model.SearchVenue
import org.beem.tastymap.search.state.SearchIntent
import org.beem.tastymap.search.state.SearchUiState
import org.beem.tastymap.ui.theme.AppColors
import org.beem.tastymap.ui.theme.getAppFontFamily

@Composable
actual fun TastySearchOverlay(
    uiState: SearchUiState,
    onIntent: (SearchIntent) -> Unit,
    modifier: Modifier
) {
    val focusManager = LocalFocusManager.current
    val fontFamily = getAppFontFamily()

    val isExpanded = uiState.isDropdownVisible && uiState.results.isNotEmpty()
    val containerShape = if (isExpanded) RoundedCornerShape(18.dp) else RoundedCornerShape(26.dp)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
            ),
        shape = containerShape,
        color = AppColors.Surface,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        border = BorderStroke(0.8.dp, AppColors.BorderLight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(containerShape)
        ) {
            // Arama Giriş Barı (50.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = AppColors.GourmetOrange
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "Ara",
                        tint = AppColors.GourmetOrange,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (uiState.query.isEmpty()) {
                        Text(
                            text = "Restoran, kafe veya lezzet ara...",
                            fontFamily = fontFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = AppColors.TextTertiary
                        )
                    }

                    BasicTextField(
                        value = uiState.query,
                        onValueChange = { onIntent(SearchIntent.QueryChanged(it)) },
                        singleLine = true,
                        textStyle = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = AppColors.TextPrimary
                        ),
                        cursorBrush = SolidColor(AppColors.GourmetOrange),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                focusManager.clearFocus()
                                onIntent(SearchIntent.SearchTriggered)
                            }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                AnimatedVisibility(
                    visible = uiState.query.isNotEmpty(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    IconButton(
                        onClick = { onIntent(SearchIntent.ClearQuery) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Clear,
                            contentDescription = "Temizle",
                            tint = AppColors.TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Genişleyen Sonuç Listesi
            if (isExpanded) {
                HorizontalDivider(
                    thickness = 0.8.dp,
                    color = AppColors.BorderLight
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 260.dp)
                ) {
                    itemsIndexed(
                        items = uiState.results,
                        key = { _, venue -> venue.placeId }
                    ) { index, venue ->
                        SearchVenueItem(
                            venue = venue,
                            onClick = {
                                focusManager.clearFocus()
                                onIntent(SearchIntent.VenueClicked(venue))
                            }
                        )

                        if (index < uiState.results.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(start = 54.dp, end = 16.dp),
                                thickness = 0.5.dp,
                                color = AppColors.BorderLight.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchVenueItem(
    venue: SearchVenue,
    onClick: () -> Unit
) {
    val fontFamily = getAppFontFamily()

    val isTastyRated = (venue.tastyMapRating ?: 0.0) > 0.0
    val activeRating = if (isTastyRated) venue.tastyMapRating else venue.googleRating
    val hasRating = activeRating != null && activeRating > 0.0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Sol İkon Rozeti (Dairesel Yumuşak Arka Plan)
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(AppColors.SurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.LocationOn,
                contentDescription = null,
                tint = AppColors.GourmetOrange,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Mekan İsmi ve Adresi
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = venue.name,
                fontFamily = fontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (!venue.vicinity.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = venue.vicinity,
                    fontFamily = fontFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = AppColors.TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Sağ Taraf: Vektörel İkonlu Puan Rozeti
        if (hasRating) {
            Spacer(modifier = Modifier.width(10.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isTastyRated) AppColors.WarmAmber.copy(alpha = 0.12f) else AppColors.SurfaceVariant,
                border = BorderStroke(
                    width = 0.5.dp,
                    color = if (isTastyRated) AppColors.WarmAmber.copy(alpha = 0.3f) else AppColors.BorderLight
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = null,
                        tint = if (isTastyRated) AppColors.WarmAmber else Color(0xFFF59E0B),
                        modifier = Modifier.size(13.dp)
                    )

                    Spacer(modifier = Modifier.width(3.dp))

                    Text(
                        text = activeRating.toString(),
                        fontFamily = fontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isTastyRated) AppColors.WarmAmber else AppColors.TextPrimary,
                        lineHeight = 12.sp
                    )
                }
            }
        }
    }
}
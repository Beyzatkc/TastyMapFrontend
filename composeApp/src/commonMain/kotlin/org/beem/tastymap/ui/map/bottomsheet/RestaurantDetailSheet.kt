package org.beem.tastymap.ui.map.bottomsheet

import androidx.compose.runtime.Composable
import org.beem.tastymap.data.model.Restaurant
import org.beem.tastymap.place.RestaurantDetailScreenModel
import org.beem.tastymap.place.model.review.ReviewItem
import org.beem.tastymap.ui.tastyview.TastyBottomSheetState
import org.beem.tastymap.ui.tastyview.TastyBox
import org.beem.tastymap.ui.tastyview.TastyColumn
import org.beem.tastymap.ui.tastyview.TastyDivider
import org.beem.tastymap.ui.tastyview.TastyIcon
import org.beem.tastymap.ui.tastyview.TastyIconButton
import org.beem.tastymap.ui.tastyview.TastyModifier
import org.beem.tastymap.ui.tastyview.TastyRow
import org.beem.tastymap.ui.tastyview.TastySpacer
import org.beem.tastymap.ui.tastyview.TastyStickyContainer
import org.beem.tastymap.ui.tastyview.TastyText
import org.beem.tastymap.ui.tastyview.TastyTextStyle
import org.beem.tastymap.ui.tastyview.TastyView
import org.beem.tastymap.ui.tastyview.icons.TastyMapIcon
import org.beem.tastymap.ui.tastyview.paginglist.TastyPagingController
import org.beem.tastymap.ui.tastyview.paginglist.TastyPagingList
import org.beem.tastymap.ui.tastyview.state.TastyStatefulView
import org.beem.tastymap.ui.tastyview.state.tastyStateOf
import org.beem.tastymap.ui.tastyview.tastylayoutenums.TastyHorizontalArrangement
import org.beem.tastymap.ui.tastyview.tastylayoutenums.TastyVerticalAlignment
import org.beem.tastymap.ui.tastyview.tastylayoutenums.TastyVerticalArrangement
import org.beem.tastymap.ui.theme.TastyMapSheetPalette

fun RestaurantDetailSheet(
    restaurant: Restaurant,
    sheetState: TastyBottomSheetState,
    palette: TastyMapSheetPalette,
    detailScreenModel: RestaurantDetailScreenModel
): TastyView {

    return TastyStickyContainer(
        modifier = TastyModifier().fillMaxWidth(),
        stickyHeader = restaurantStickyHeader(
            restaurant = restaurant,
            sheetState = sheetState,
            palette = palette
        ),
        scrollableContent = TastyPagingList<ReviewItem>(
            modifier = TastyModifier().fillMaxWidth(),
            controller = TastyPagingController(pageSize = 5) { page, pageSize ->
                detailScreenModel.loadReviews(restaurant.id, page, pageSize)
            },
            headerTemplate = {
                buildRestaurantStaticInfo(restaurant, palette)
            },
            itemTemplate = { review ->
                ReviewRowItem(review = review, palette = palette)
            },
            loadingTemplate = {
                TastyRow(
                    modifier = TastyModifier().fillMaxWidth().padding(16),
                    horizontalArrangement = TastyHorizontalArrangement.Center,
                    children = listOf(
                        TastyText(
                            text = "Yorumlar yükleniyor...",
                            style = TastyTextStyle.BODY,
                            color = palette.subtitleColor
                        )
                    )
                )
            }
        )
    )
}

private fun ReviewRowItem(review: ReviewItem, palette: TastyMapSheetPalette): TastyView {
    // Enum'a göre ikon seçimi (Güvenli yapı!)
    val sourceIcon = TastyMapIcon.STAR

    return TastyBox(
        modifier = TastyModifier()
            .fillMaxWidth()
            .padding(left = 16, right = 16, top = 6, bottom = 6)
            .background(palette.dividerColor)
            .borderRadius(12)
            .padding(14),
        children = listOf(
            TastyColumn(
                verticalArrangement = TastyVerticalArrangement.SpacedBy(6),
                children = listOf(
                    TastyRow(
                        modifier = TastyModifier().fillMaxWidth(),
                        horizontalArrangement = TastyHorizontalArrangement.SpaceBetween,
                        verticalAlignment = TastyVerticalAlignment.Center,
                        children = listOf(
                            TastyText(
                                text = review.name,
                                style = TastyTextStyle.BODY,
                                color = palette.titleColor
                            ),
                            TastyRow(
                                verticalAlignment = TastyVerticalAlignment.Center,
                                horizontalArrangement = TastyHorizontalArrangement.SpacedBy(4),
                                children = listOf(
                                    TastyIcon(icon = sourceIcon, color = "#F59E0B", sizePx = 12),
                                    TastyText(
                                        text = review.rating.toString(),
                                        style = TastyTextStyle.BODY,
                                        color = palette.ratingTextColor
                                    )
                                )
                            )
                        )
                    ),
                    ReviewContentBox(
                        review,
                        palette
                    )
                )
            )
        )
    )
}

private fun buildRestaurantStaticInfo(restaurant: Restaurant, palette: TastyMapSheetPalette): TastyView {
    val isOperational = restaurant.status == "OPERATIONAL" || restaurant.status == "Açık"
    val statusText = if (isOperational) "Açık" else "Kapalı"
    val categoryText = restaurant.category.ifBlank { "Restoran" }.uppercase()

    return TastyColumn(
        modifier = TastyModifier().fillMaxWidth().padding(16),
        verticalArrangement = TastyVerticalArrangement.SpacedBy(16),
        children = listOf(
            // Kategori alanı
            TastyRow(
                verticalAlignment = TastyVerticalAlignment.Center,
                horizontalArrangement = TastyHorizontalArrangement.SpacedBy(8),
                children = listOf(
                    TastyIcon(icon = TastyMapIcon.STAR, color = "#F59E0B", sizePx = 20),
                    TastyText(text = categoryText, style = TastyTextStyle.BADGE, color = "#F59E0B")
                )
            ),
            // Restoran ismi ve durumu
            TastyColumn(
                verticalArrangement = TastyVerticalArrangement.SpacedBy(4),
                children = listOf(
                    TastyText(text = restaurant.name, style = TastyTextStyle.TITLE, color = palette.titleColor),
                    TastyText(text = statusText, style = TastyTextStyle.BODY, color = palette.subtitleColor)
                )
            ),
            TastySpacer(
                modifier = TastyModifier().height(4)
            ),

            TastyRow(
                modifier = TastyModifier().fillMaxWidth(),
                horizontalArrangement = TastyHorizontalArrangement.SpacedBy(12),
                children = listOf(
                    TastyBox(
                        modifier = TastyModifier()
                            .weight(1f)
                            .fillMaxWidth()
                            .background(palette.dividerColor)
                            .borderRadius(16)
                            .padding(12),
                        children = listOf(
                            TastyRow(
                                verticalAlignment = TastyVerticalAlignment.Center,
                                horizontalArrangement = TastyHorizontalArrangement.SpacedBy(6),
                                children = listOf(
                                    TastyIcon(icon = TastyMapIcon.STAR, color = palette.ratingTextColor, sizePx = 16),
                                    TastyText(text = "${restaurant.rating} Skor", style = TastyTextStyle.BODY, color = palette.ratingTextColor)
                                )
                            )
                        )
                    ),
                    TastyBox(
                        modifier = TastyModifier()
                            .weight(1f)
                            .fillMaxWidth()
                            .background(palette.dividerColor)
                            .borderRadius(16)
                            .padding(12),
                        children = listOf(
                            TastyRow(
                                verticalAlignment = TastyVerticalAlignment.Center,
                                horizontalArrangement = TastyHorizontalArrangement.SpacedBy(6),
                                children = listOf(
                                    TastyIcon(icon = TastyMapIcon.LOCATION, color = palette.primaryColor, sizePx = 16),
                                    TastyText(text = "Hızlı Rota", style = TastyTextStyle.BODY, color = palette.primaryColor)
                                )
                            )
                        )
                    )
                )
            ),

            // 4. ADRES SATIRI
            TastyRow(
                modifier = TastyModifier().fillMaxWidth(),
                verticalAlignment = TastyVerticalAlignment.Center,
                horizontalArrangement = TastyHorizontalArrangement.SpacedBy(8),
                children = listOf(
                    TastyIcon(icon = TastyMapIcon.LOCATION, color = palette.primaryColor, sizePx = 18),
                    TastyText(text = restaurant.address, style = TastyTextStyle.BODY, color = palette.subtitleColor)
                )
            ),

            TastyDivider(
                modifier = TastyModifier()
                    .stickyTrigger()
                    .marginTop(4)
                    .marginBottom(4),
                color = palette.dividerColor,
                thickness = 1,
            ),

            TastyText(text = "Öne Çıkan Yorumlar", style = TastyTextStyle.SUBTITLE, color = palette.titleColor)
        )
    )
}

private fun restaurantStickyHeader(
    restaurant: Restaurant,
    sheetState: TastyBottomSheetState,
    palette: TastyMapSheetPalette
): TastyView {
    return TastyColumn(
        modifier = TastyModifier()
            .fillMaxWidth()
            .background(palette.backgroundColor),
        children = listOf(
            // 📌 1. ANA SATIR: Başlık ve Kapatma Butonu
            TastyRow(
                modifier = TastyModifier()
                    .fillMaxWidth()
                    .padding(top = 16, bottom = 8, left = 16, right = 16),
                horizontalArrangement = TastyHorizontalArrangement.SpaceBetween,
                verticalAlignment = TastyVerticalAlignment.Center,
                children = listOf(
                    // Başlık Alanı (Gerektiğinde taşmayı önlemek için weight veya sarmal eklenebilir)
                    TastyColumn(
                        modifier = TastyModifier().weight(1f),
                        verticalArrangement = TastyVerticalArrangement.SpacedBy(2),
                        children = listOf(
                            TastyText(
                                text = restaurant.name,
                                style = TastyTextStyle.TITLE,
                                color = palette.titleColor
                            ),
                            // "Öne Çıkan Yorumlar" yazısını başlığın altına zarif bir alt başlık olarak aldık
                            TastyText(
                                text = "Öne Çıkan Yorumlar",
                                style = TastyTextStyle.BODY, // Varsa CAPTION/SUBTITLE stili daha tatlı olur
                                color = palette.subtitleColor
                            )
                        )
                    ),

                    // Modern Kapatma Butonu
                    TastyIconButton(
                        modifier = TastyModifier()
                            .size(32, 32)
                            .background(palette.closeButtonBackground)
                            .borderRadius(50),
                        iconHtml = "&times;",
                        backgroundColor = palette.closeButtonBackground,
                        iconColor = palette.closeButtonIconColor,
                        onClick = { sheetState.close() }
                    )
                )
            ),

            // 📌 2. ZARİF ALT SINIR (Sticky hissini pekiştiren ince çizgi)
            TastyDivider(
                modifier = TastyModifier()
                    .fillMaxWidth()
                    .marginTop(8),
                color = palette.dividerColor,
                thickness = 1
            )
        )
    )
}


private fun ReviewContentBox(review: ReviewItem, palette: TastyMapSheetPalette): TastyView {
    data class RowItemState(
        val isExpanded: Boolean = false,
        val hasOverflow: Boolean = false
    )

    val rowState = tastyStateOf(RowItemState())

    // 🚀 Adım 2: StatefulView sarmalayıcısını çakıyoruz
    return TastyStatefulView(rowState) { state ->
        TastyColumn(
            verticalArrangement = TastyVerticalArrangement.SpacedBy(6),
            children = listOf(
                TastyText(
                    text = "\"${review.content}\"",
                    style = TastyTextStyle.BODY,
                    color = palette.subtitleColor,
                    maxLines = if (state.isExpanded) Int.MAX_VALUE else 3,
                    onOverflow = {
                        rowState.value = rowState.value.copy(hasOverflow = true)
                    }
                ),
                if (state.hasOverflow || state.isExpanded) {
                    TastyRow(
                        modifier = TastyModifier()
                            .padding(top = 4)
                            .clickable {
                                rowState.value = rowState.value.copy(isExpanded = !state.isExpanded) },
                        children = listOf(
                            TastyText(
                                text = if (state.isExpanded) "Daha Az Kapat" else "Devamını Gör",
                                style = TastyTextStyle.BADGE,
                                color = "#2563EB"
                            )
                        )
                    )
                } else {
                    // 🚀 Kısa yorumlarda burası boş dönecek, buton asla tasarıma sızamayacak!
                    TastySpacer(modifier = TastyModifier().height(0))
                }
            )
        )
    }
}
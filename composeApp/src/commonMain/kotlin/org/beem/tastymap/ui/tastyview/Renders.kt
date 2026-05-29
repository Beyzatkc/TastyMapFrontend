package org.beem.tastymap.ui.tastyview

import org.beem.tastymap.data.model.Restaurant
import org.beem.tastymap.ui.icons.TastyMapIcons
import org.beem.tastymap.ui.theme.TastyMapSheetPalette
import org.beem.tastymap.ui.map.bottomsheet.RestaurantAction



fun buildRestaurantSheetUI(
    restaurant: Restaurant,
    sheetState: TastyBottomSheetState,
    palette: TastyMapSheetPalette,
    onAction: (RestaurantAction) -> Unit
): TastyView {
    // 🎯 1. DUPDURU DURUM KONTROLÜ
    val isOperational = restaurant.status == "OPERATIONAL" || restaurant.status == "Açık"
    val statusText = if (isOperational) "Açık" else "Kapalı"
    val statusColor = if (isOperational) palette.successColor else palette.errorColor

    // Güvenli veri dönüşümleri (Null Safety)
    val ratingText = restaurant.rating?.toString() ?: "0.0"
    val totalRatingsText = restaurant.totalRatings?.let { "($it değerlendirme)" } ?: "(Yorum yok)"
    val categoryText = restaurant.category.ifBlank { "Restoran" }.uppercase()

    return TastyColumn(
        modifier = TastyModifier().fillMaxWidth().flexProperties(g = 16),
        children = listOf(

            // 1. ÜST AKSİYON SATIRI: Rozet ve Kapatma Butonu
            TastyRow(
                modifier = TastyModifier().fillMaxWidth().flexProperties(dir = "row", justify = "space-between", align = "center"),
                children = listOf(
                    TastyRow(
                        modifier = TastyModifier().flexProperties(dir = "row", align = "center", g = 8),
                        children = listOf(
                            TastyIcon(icon = TastyMapIcons.STAR, color = "#F59E0B", sizePx = 20),
                            TastyText(text = categoryText, style = TastyTextStyle.BADGE, color = "#F59E0B")
                        )
                    ),
                    // Kapatma butonu: State üzerinden jenerik animasyonlu kapatmayı tetikliyor!
                    TastyIconButton(
                        iconHtml = "&times;",
                        backgroundColor = palette.closeButtonBackground,
                        iconColor = palette.closeButtonIconColor,
                        onClick = {
                            sheetState.close()
                        }
                    )
                )
            ),

            // 2. ANA BAŞLIK ALANI (Tamamen API'den gelen Restoran İsmi)
            TastyColumn(
                modifier = TastyModifier().flexProperties(g = 4),
                children = listOf(
                    TastyText(text = restaurant.name, style = TastyTextStyle.TITLE, color = palette.titleColor),
                    TastyRow(
                        modifier = TastyModifier().flexProperties(dir = "row", align = "center", g = 6),
                        children = listOf(
                            TastySpacer(sizePx = 2),
                            TastyText(text = statusText, style = TastyTextStyle.BODY, color = palette.subtitleColor)
                        )
                    )
                )
            ),

        TastySpacer(sizePx = 4),

        // 3. İSTATİSTİK KARTLARI (Skor ve Özellikler)
        TastyRow(
            modifier = TastyModifier().fillMaxWidth().flexProperties(dir = "row", justify = "space-between", g = 12),
            children = listOf(
                TastyCard(
                    backgroundColor = palette.dividerColor,
                    cornerRadius = 16,
                    padding = 12,
                    children = listOf(
                        TastyRow(
                            modifier = TastyModifier().flexProperties(dir = "row", align = "center", g = 6),
                            children = listOf(
                                TastyIcon(icon = TastyMapIcons.STAR, color = palette.ratingTextColor, sizePx = 16),
                                TastyText(text = "$ratingText Skor", style = TastyTextStyle.BODY, color = palette.ratingTextColor)
                            )
                        )
                    )
                ),
                TastyCard(
                    backgroundColor = palette.dividerColor,
                    cornerRadius = 16,
                    padding = 12,
                    children = listOf(
                        TastyRow(
                            modifier = TastyModifier().flexProperties(dir = "row", align = "center", g = 6),
                            children = listOf(
                                TastyIcon(icon = TastyMapIcons.LOCATION, color = palette.primaryColor, sizePx = 16),
                                TastyText(text = "Hızlı Rota", style = TastyTextStyle.BODY, color = palette.primaryColor)
                            )
                        )
                    )
                )
            )
        ),

        // 4. ADRES SATIRI
        TastyRow(
            modifier = TastyModifier().fillMaxWidth().flexProperties(dir = "row", align = "center", g = 8),
            children = listOf(
                TastyIcon(icon = TastyMapIcons.LOCATION, color = palette.primaryColor, sizePx = 18),
                TastyText(text = restaurant.address, style = TastyTextStyle.BODY, color = palette.subtitleColor)
            )
        ),

        TastyDivider(color = palette.dividerColor, thicknessPx = 1, marginTop = 4, marginBottom = 4),

        // 5. YORUMLAR BAŞLIĞI VE LİSTESİ
        TastyText(text = "Öne Çıkan Yorumlar", style = TastyTextStyle.SUBTITLE, color = palette.titleColor),

        // Burası bir sonraki adımda API'den (List<Review>) gelecek dayıcım, şimdilik şablon korundu
        TastyLazyColumn(
            key = "restaurant-reviews-${restaurant.id}", // Her restoranın yorum listesi eşsiz olsun
            items = listOf(
                TastyCard(
                    backgroundColor = palette.dividerColor,
                    cornerRadius = 12,
                    padding = 14,
                    children = listOf(
                        TastyRow(
                            modifier = TastyModifier().fillMaxWidth().flexProperties(dir = "row", justify = "space-between", align = "center"),
                            children = listOf(
                                TastyText(text = "Ahmet Yılmaz", style = TastyTextStyle.BODY, color = palette.titleColor),
                                TastyRow(
                                    modifier = TastyModifier().flexProperties(dir = "row", align = "center", g = 4),
                                    children = listOf(
                                        TastyIcon(icon = TastyMapIcons.STAR, color = "#F59E0B", sizePx = 12),
                                        TastyText(text = "5.0", style = TastyTextStyle.BODY, color = palette.ratingTextColor)
                                    )
                                )
                            )
                        ),
                        TastySpacer(sizePx = 6),
                        TastyText(text = "\"Kahveleri gerçekten çok başarılı, harika bir atmosferi var. Tavsiye ederim!\"", style = TastyTextStyle.BODY, color = palette.subtitleColor)
                    )
                )
            ),
            onLoadMore = {
                // API'den daha fazla yorum çekmek için aksiyon fırlatıyoruz!
                onAction(RestaurantAction.Share) // Veya senin tanımlayacağın LoadMoreReviews aksiyonu
            }
        ),
    )
    )
}
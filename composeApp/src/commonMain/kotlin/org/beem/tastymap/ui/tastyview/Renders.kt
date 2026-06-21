package org.beem.tastymap.ui.tastyview

import org.beem.tastymap.data.model.Restaurant
import org.beem.tastymap.ui.tastyview.icons.TastyMapIcon
import org.beem.tastymap.ui.theme.TastyMapSheetPalette
import org.beem.tastymap.ui.map.bottomsheet.RestaurantAction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.beem.tastymap.ui.tastyview.tastylayoutenums.TastyHorizontalArrangement
import org.beem.tastymap.ui.tastyview.tastylayoutenums.TastyVerticalAlignment
import org.beem.tastymap.ui.tastyview.tastylayoutenums.TastyVerticalArrangement

@Composable
fun TestRestaurantSheetUI(
    restaurant: Restaurant,
    sheetState: TastyBottomSheetState,
    palette: TastyMapSheetPalette,
    onAction: (RestaurantAction) -> Unit
) {
    // 🎯 1. DUPDURU DURUM KONTROLÜ
    val isOperational = restaurant.status == "OPERATIONAL" || restaurant.status == "Açık"
    val statusText = if (isOperational) "Açık" else "Kapalı"

    // Güvenli veri dönüşümleri (Null Safety)
    val ratingText = restaurant.rating?.toString() ?: "0.0"
    val categoryText = restaurant.category.ifBlank { "Restoran" }.uppercase()

    // 🚀 TastyColumn(modifier = TastyModifier().fillMaxWidth().layout(gapDp = 16)) Karşılığı:
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), // Dış çerçeve boşluğu
        verticalArrangement = Arrangement.spacedBy(16.dp) // gapDp = 16 zekası
    ) {

        // 1. ÜST AKSİYON SATIRI: Rozet ve Kapatma Butonu
        // TastyRow(modifier = TastyModifier().fillMaxWidth().layout(direction = ROW, justify = SPACE_BETWEEN, align = CENTER))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // İçteki Kategori Bilgisi: TastyRow(modifier = TastyModifier().layout(direction = ROW, align = CENTER, gapDp = 8))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // TastyIcon(icon = TastyMapIcons.STAR, defaultColor = "#F59E0B", sizePx = 20)
                Text(
                    text = "★", // İkon mekanizmanızı simüle eden karakter veya gerçek Icon
                    color = Color(0xFFF59E0B),
                    fontSize = 20.sp
                )
                // TastyText(text = categoryText, style = TastyTextStyle.BADGE, defaultColor = "#F59E0B")
                Text(
                    text = categoryText,
                    color = Color(0xFFF59E0B),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // TastyIconButton(iconHtml = "&times;", backgroundColor = ..., iconColor = ...)
            IconButton(
                onClick = { sheetState.close() },
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = palette.closeButtonBackground.toComposeColor(),
                        shape = RoundedCornerShape(50.dp) // Tam yuvarlak buton
                    )
            ) {
                Text(
                    text = "×",
                    color = palette.closeButtonIconColor.toComposeColor(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 2. ANA BAŞLIK ALANI (Tamamen API'den gelen Restoran İsmi)
        // TastyColumn(modifier = TastyModifier().layout(gapDp = 4))
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            // TastyText(text = restaurant.name, style = TastyTextStyle.TITLE, defaultColor = palette.titleColor)
            Text(
                text = restaurant.name,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = palette.titleColor.toComposeColor()
            )
            // TastyRow(modifier = TastyModifier().layout(direction = ROW, align = CENTER, gapDp = 6))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // TastySpacer(sizePx = 2)
                Spacer(modifier = Modifier.width(2.dp))
                // TastyText(text = statusText, style = TastyTextStyle.BODY, defaultColor = palette.subtitleColor)
                Text(
                    text = statusText,
                    fontSize = 14.sp,
                    color = palette.subtitleColor.toComposeColor()
                )
            }
        }

        // TastySpacer(sizePx = 4)
        Spacer(modifier = Modifier.height(4.dp))

        // 3. İSTATİSTİK KARTLARI (Skor ve Özellikler)
        // TastyRow(modifier = TastyModifier().fillMaxWidth().layout(direction = ROW, justify = SPACE_BETWEEN, gapDp = 12))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp) // Kartlar arası 12dp boşluk
        ) {
            // İlk Kart: TastyCard(modifier = TastyModifier().weight(1f), backgroundColor = palette.dividerColor, cornerRadius = 16, padding = 12)
            Box(
                modifier = Modifier
                    .weight(1f) // İki kartın yan yana sığması için Compose'un orijinal kuralı
                    .background(
                        color = palette.dividerColor.toComposeColor(),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(12.dp)
            ) {
                // İçteki Satır: TastyRow(modifier = TastyModifier().layout(direction = ROW, align = CENTER, gapDp = 6))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("★", color = palette.ratingTextColor.toComposeColor(), fontSize = 16.sp)
                    Text("$ratingText Skor", color = palette.ratingTextColor.toComposeColor(), fontSize = 14.sp)
                }
            }

            // İkinci Kart: TastyCard(modifier = TastyModifier().weight(1f), backgroundColor = palette.dividerColor, cornerRadius = 16, padding = 12)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        color = palette.dividerColor.toComposeColor(),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(12.dp)
            ) {
                // İçteki Satır: TastyRow(modifier = TastyModifier().layout(direction = ROW, align = CENTER, gapDp = 6))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("📍", color = palette.primaryColor.toComposeColor(), fontSize = 16.sp)
                    Text("Hızlı Rota", color = palette.primaryColor.toComposeColor(), fontSize = 14.sp)
                }
            }
        }

        // 4. ADRES SATIRI
        // TastyRow(modifier = TastyModifier().fillMaxWidth().layout(direction = ROW, align = CENTER, gapDp = 8))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("📍", color = palette.primaryColor.toComposeColor(), fontSize = 18.sp)
            Text(
                text = restaurant.address,
                fontSize = 14.sp,
                color = palette.subtitleColor.toComposeColor()
            )
        }

        // TastyDivider(defaultColor = palette.dividerColor, thicknessPx = 1, marginTop = 4, marginBottom = 4)
        HorizontalDivider(
            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
            thickness = 1.dp,
            color = palette.dividerColor.toComposeColor()
        )

        // 5. YORUMLAR BAŞLIĞI
        // TastyText(text = "Öne Çıkan Yorumlar", style = TastyTextStyle.SUBTITLE, defaultColor = palette.titleColor)
        Text(
            text = "Öne Çıkan Yorumlar",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = palette.titleColor.toComposeColor()
        )

        // TastyLazyColumn İçindeki Tekil Kart Yapısı:
        // TastyCard(modifier = TastyModifier().fillMaxWidth(), backgroundColor = palette.dividerColor, cornerRadius = 12, padding = 14)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = palette.dividerColor.toComposeColor(),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                // Yorum Kartının Üst Satırı: TastyRow(modifier = TastyModifier().fillMaxWidth().layout(direction = ROW, justify = SPACE_BETWEEN, align = CENTER))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Ahmet Yılmaz İsmi (Sol Uçta)
                    Text(
                        text = "Ahmet Yılmaz",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = palette.titleColor.toComposeColor()
                    )
                    // Yıldız + 5.0 (Sağ Uçta)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("★", color = Color(0xFFF59E0B), fontSize = 12.sp)
                        Text(
                            text = "5.0",
                            fontSize = 12.sp,
                            color = palette.ratingTextColor.toComposeColor()
                        )
                    }
                }

                // TastySpacer(sizePx = 6) -> Column'ın spacedBy(6.dp) kuralı bunu otomatik hallediyor zaten dayıcım

                // Yorumun Kendisi: TastyText(text = "...", style = TastyTextStyle.BODY, defaultColor = palette.subtitleColor)
                Text(
                    text = "\"Kahveleri gerçekten çok başarılı, harika bir atmosferi var. Tavsiye ederim!\"",
                    fontSize = 14.sp,
                    color = palette.subtitleColor.toComposeColor()
                )
            }
        }
    }
}


fun StickyTest(
    restaurant: Restaurant,
    sheetState: TastyBottomSheetState,
    palette: TastyMapSheetPalette,
    onAction: (RestaurantAction) -> Unit
): TastyView{
    return TastyStickyContainer(
        modifier = TastyModifier().fillMaxWidth(),

        stickyHeader = TastyRow(
            modifier = TastyModifier()
                .fillMaxWidth()
                .background(palette.backgroundColor) // Arkası opak olsun ki alttan kayan yazılar görünmesin
                .padding(top = 12, bottom = 12, left = 16, right = 16),
            horizontalArrangement = TastyHorizontalArrangement.SpaceBetween,
            verticalAlignment = TastyVerticalAlignment.Center,
            children = listOf(
                // Sol tarafta küçük restoran ismi
                TastyText(text = restaurant.name, style = TastyTextStyle.TITLE, color = palette.titleColor),

                // Sağ tarafta o küçük kapatma butonu
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

        scrollableContent = buildRestaurantSheetUI(
            restaurant = restaurant,
            sheetState = sheetState,
            palette = palette,
            onAction = onAction
        )
    )
}

fun buildRestaurantSheetUI(
    restaurant: Restaurant,
    sheetState: TastyBottomSheetState,
    palette: TastyMapSheetPalette,
    onAction: (RestaurantAction) -> Unit
): TastyColumn {
    // 🎯 1. DUPDURU DURUM KONTROLÜ
    val isOperational = restaurant.status == "OPERATIONAL" || restaurant.status == "Açık"
    val statusText = if (isOperational) "Açık" else "Kapalı"
    val statusColor = if (isOperational) palette.successColor else palette.errorColor

    // Güvenli veri dönüşümleri (Null Safety)
    val ratingText = restaurant.rating?.toString() ?: "0.0"
    val totalRatingsText = restaurant.totalRatings?.let { "($it değerlendirme)" } ?: "(Yorum yok)"
    val categoryText = restaurant.category.ifBlank { "Restoran" }.uppercase()

    return TastyColumn(
        modifier = TastyModifier()
            .fillMaxWidth()
            .padding(16),
        verticalArrangement = TastyVerticalArrangement.SpacedBy(16),
        scrollable = false,
        children = listOf(

            // 1. ÜST AKSİYON SATIRI: Rozet ve Kapatma Butonu
            TastyRow(
                modifier = TastyModifier().fillMaxWidth(),
                horizontalArrangement = TastyHorizontalArrangement.SpaceBetween,
                verticalAlignment = TastyVerticalAlignment.Center,
                children = listOf(
                    TastyRow(
                        verticalAlignment = TastyVerticalAlignment.Center,
                        horizontalArrangement = TastyHorizontalArrangement.SpacedBy(8),

                        children = listOf(
                            TastyIcon(icon = TastyMapIcon.STAR, color = "#F59E0B", sizePx = 20),
                            TastyText(text = categoryText, style = TastyTextStyle.BADGE, color = "#F59E0B")
                        )
                    ),
                    // Kapatma butonu: State üzerinden jenerik animasyonlu kapatmayı tetikliyor!
                    TastyIconButton(
                        modifier = TastyModifier()
                            .size(36, 36)
                            .background(palette.closeButtonBackground)
                            .borderRadius(50),
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
                verticalArrangement = TastyVerticalArrangement.SpacedBy(4),
                children = listOf(
                    TastyText(text = restaurant.name, style = TastyTextStyle.TITLE, color = palette.titleColor),
                    TastyRow(
                        verticalAlignment = TastyVerticalAlignment.Center,
                        horizontalArrangement = TastyHorizontalArrangement.SpacedBy(6),

                        children = listOf(
                            TastySpacer(modifier = TastyModifier().width(2)),
                            TastyText(text = statusText, style = TastyTextStyle.BODY, color = palette.subtitleColor)
                        )
                    )
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
                                TastyText(text = "$ratingText Skor", style = TastyTextStyle.BODY, color = palette.ratingTextColor)
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

        // 5. YORUMLAR BAŞLIĞI VE LİSTESİ
        TastyText(text = "Öne Çıkan Yorumlar", style = TastyTextStyle.SUBTITLE, color = palette.titleColor),

        // Burası bir sonraki adımda API'den (List<Review>) gelecek dayıcım, şimdilik şablon korundu
        TastyBox(
            modifier = TastyModifier().fillMaxWidth()
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
                                    modifier = TastyModifier().weight(1f),
                                    text = "Ahmet Yılmaz",
                                    style = TastyTextStyle.BODY,
                                    color = palette.titleColor
                                ),
                                TastyRow(
                                    verticalAlignment = TastyVerticalAlignment.Center,
                                    horizontalArrangement = TastyHorizontalArrangement.SpacedBy(4),
                                    children = listOf(
                                        TastyIcon(icon = TastyMapIcon.STAR, color = "#F59E0B", sizePx = 12),
                                        TastyText(text = "5.0", style = TastyTextStyle.BODY, color = palette.ratingTextColor)
                                    )
                                )
                            )
                        ),

                        TastyText(
                            text = "\"Kahveleri gerçekten çok başarılı, harika bir atmosferi var. Tavsiye ederim!\"",
                            style = TastyTextStyle.BODY,
                            color = palette.subtitleColor
                        )
                    )
                )
            )
        ),
            TastyBox(
                modifier = TastyModifier().fillMaxWidth()
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
                                        modifier = TastyModifier().weight(1f),
                                        text = "Ahmet Yılmaz",
                                        style = TastyTextStyle.BODY,
                                        color = palette.titleColor
                                    ),
                                    TastyRow(
                                        verticalAlignment = TastyVerticalAlignment.Center,
                                        horizontalArrangement = TastyHorizontalArrangement.SpacedBy(4),
                                        children = listOf(
                                            TastyIcon(icon = TastyMapIcon.STAR, color = "#F59E0B", sizePx = 12),
                                            TastyText(text = "5.0", style = TastyTextStyle.BODY, color = palette.ratingTextColor)
                                        )
                                    )
                                )
                            ),

                            TastyText(
                                text = "\"Kahveleri gerçekten çok başarılı, harika bir atmosferi var. Tavsiye ederim!\"",
                                style = TastyTextStyle.BODY,
                                color = palette.subtitleColor
                            )
                        )
                    )
                )
            ),
            TastyBox(
                modifier = TastyModifier().fillMaxWidth()
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
                                        modifier = TastyModifier().weight(1f),
                                        text = "Ahmet Yılmaz",
                                        style = TastyTextStyle.BODY,
                                        color = palette.titleColor
                                    ),
                                    TastyRow(
                                        verticalAlignment = TastyVerticalAlignment.Center,
                                        horizontalArrangement = TastyHorizontalArrangement.SpacedBy(4),
                                        children = listOf(
                                            TastyIcon(icon = TastyMapIcon.STAR, color = "#F59E0B", sizePx = 12),
                                            TastyText(text = "5.0", style = TastyTextStyle.BODY, color = palette.ratingTextColor)
                                        )
                                    )
                                )
                            ),

                            TastyText(
                                text = "\"Kahveleri gerçekten çok başarılı, harika bir atmosferi var. Tavsiye ederim!\"",
                                style = TastyTextStyle.BODY,
                                color = palette.subtitleColor
                            )
                        )
                    )
                )
            ),
            TastyBox(
                modifier = TastyModifier().fillMaxWidth()
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
                                        modifier = TastyModifier().weight(1f),
                                        text = "Ahmet Yılmaz",
                                        style = TastyTextStyle.BODY,
                                        color = palette.titleColor
                                    ),
                                    TastyRow(
                                        verticalAlignment = TastyVerticalAlignment.Center,
                                        horizontalArrangement = TastyHorizontalArrangement.SpacedBy(4),
                                        children = listOf(
                                            TastyIcon(icon = TastyMapIcon.STAR, color = "#F59E0B", sizePx = 12),
                                            TastyText(text = "5.0", style = TastyTextStyle.BODY, color = palette.ratingTextColor)
                                        )
                                    )
                                )
                            ),

                            TastyText(
                                text = "\"Kahveleri gerçekten çok başarılı, harika bir atmosferi var. Tavsiye ederim!\"",
                                style = TastyTextStyle.BODY,
                                color = palette.subtitleColor
                            )
                        )
                    )
                )
            ),
            TastyBox(
                modifier = TastyModifier().fillMaxWidth()
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
                                        modifier = TastyModifier().weight(1f),
                                        text = "Ahmet Yılmaz",
                                        style = TastyTextStyle.BODY,
                                        color = palette.titleColor
                                    ),
                                    TastyRow(
                                        verticalAlignment = TastyVerticalAlignment.Center,
                                        horizontalArrangement = TastyHorizontalArrangement.SpacedBy(4),
                                        children = listOf(
                                            TastyIcon(icon = TastyMapIcon.STAR, color = "#F59E0B", sizePx = 12),
                                            TastyText(text = "5.0", style = TastyTextStyle.BODY, color = palette.ratingTextColor)
                                        )
                                    )
                                )
                            ),

                            TastyText(
                                text = "\"Kahveleri gerçekten çok başarılı, harika bir atmosferi var. Tavsiye ederim!\"",
                                style = TastyTextStyle.BODY,
                                color = palette.subtitleColor
                            )
                        )
                    )
                )
            ),
            TastyBox(
                modifier = TastyModifier().fillMaxWidth()
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
                                        modifier = TastyModifier().weight(1f),
                                        text = "Ahmet Yılmaz",
                                        style = TastyTextStyle.BODY,
                                        color = palette.titleColor
                                    ),
                                    TastyRow(
                                        verticalAlignment = TastyVerticalAlignment.Center,
                                        horizontalArrangement = TastyHorizontalArrangement.SpacedBy(4),
                                        children = listOf(
                                            TastyIcon(icon = TastyMapIcon.STAR, color = "#F59E0B", sizePx = 12),
                                            TastyText(text = "5.0", style = TastyTextStyle.BODY, color = palette.ratingTextColor)
                                        )
                                    )
                                )
                            ),

                            TastyText(
                                text = "\"Kahveleri gerçekten çok başarılı, harika bir atmosferi var. Tavsiye ederim!\"",
                                style = TastyTextStyle.BODY,
                                color = palette.subtitleColor
                            )
                        )
                    )
                )
            ),
            ahmetYilmaz(palette),
            ahmetYilmaz(palette),
            ahmetYilmaz(palette),
            ahmetYilmaz(palette)
    )
    )
}


fun ahmetYilmaz(palette: TastyMapSheetPalette): TastyView{
    return TastyBox(
        modifier = TastyModifier().fillMaxWidth()
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
                                modifier = TastyModifier().weight(1f),
                                text = "Ahmet Yılmaz",
                                style = TastyTextStyle.BODY,
                                color = palette.titleColor
                            ),
                            TastyRow(
                                verticalAlignment = TastyVerticalAlignment.Center,
                                horizontalArrangement = TastyHorizontalArrangement.SpacedBy(4),
                                children = listOf(
                                    TastyIcon(icon = TastyMapIcon.STAR, color = "#F59E0B", sizePx = 12),
                                    TastyText(text = "5.0", style = TastyTextStyle.BODY, color = palette.ratingTextColor)
                                )
                            )
                        )
                    ),

                    TastyText(
                        text = "\"Kahveleri gerçekten çok başarılı, harika bir atmosferi var. Tavsiye ederim!\"",
                        style = TastyTextStyle.BODY,
                        color = palette.subtitleColor
                    )
                )
            )
        )
    )
}
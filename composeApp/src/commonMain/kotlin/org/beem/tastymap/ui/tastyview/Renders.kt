package org.beem.tastymap.ui.tastyview

import org.beem.tastymap.data.model.Restaurant
import org.beem.tastymap.ui.icons.TastyMapIcons
import org.beem.tastymap.ui.theme.TastyMapSheetPalette
import org.beem.tastymap.ui.map.bottomsheet.RestaurantAction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.beem.tastymap.ui.tastyview.tastylayoutenums.TastyBoxAlignment
import org.beem.tastymap.ui.tastyview.tastylayoutenums.TastyHorizontalAlignment
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
                // TastyIcon(icon = TastyMapIcons.STAR, color = "#F59E0B", sizePx = 20)
                Text(
                    text = "★", // İkon mekanizmanızı simüle eden karakter veya gerçek Icon
                    color = Color(0xFFF59E0B),
                    fontSize = 20.sp
                )
                // TastyText(text = categoryText, style = TastyTextStyle.BADGE, color = "#F59E0B")
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
            // TastyText(text = restaurant.name, style = TastyTextStyle.TITLE, color = palette.titleColor)
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
                // TastyText(text = statusText, style = TastyTextStyle.BODY, color = palette.subtitleColor)
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

        // TastyDivider(color = palette.dividerColor, thicknessPx = 1, marginTop = 4, marginBottom = 4)
        HorizontalDivider(
            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
            thickness = 1.dp,
            color = palette.dividerColor.toComposeColor()
        )

        // 5. YORUMLAR BAŞLIĞI
        // TastyText(text = "Öne Çıkan Yorumlar", style = TastyTextStyle.SUBTITLE, color = palette.titleColor)
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

                // Yorumun Kendisi: TastyText(text = "...", style = TastyTextStyle.BODY, color = palette.subtitleColor)
                Text(
                    text = "\"Kahveleri gerçekten çok başarılı, harika bir atmosferi var. Tavsiye ederim!\"",
                    fontSize = 14.sp,
                    color = palette.subtitleColor.toComposeColor()
                )
            }
        }
    }
}

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
        modifier = TastyModifier()
            .fillMaxWidth()
            .padding(16),
        verticalArrangement = TastyVerticalArrangement.SpacedBy(16),
        children = listOf(

            // 1. ÜST AKSİYON SATIRI: Rozet ve Kapatma Butonu
            TastyRow(
                modifier = TastyModifier().fillMaxWidth(),
                horizontalArrangement = TastyHorizontalArrangement.SpaceBetween,
                verticalAlignment = TastyVerticalAlignment.CENTER,
                children = listOf(
                    TastyRow(
                        verticalAlignment = TastyVerticalAlignment.CENTER,
                        horizontalArrangement = TastyHorizontalArrangement.SpacedBy(8),

                        children = listOf(
                            TastyIcon(icon = TastyMapIcons.STAR, color = "#F59E0B", sizePx = 20),
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
                        verticalAlignment = TastyVerticalAlignment.CENTER,
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
                            verticalAlignment = TastyVerticalAlignment.CENTER,
                            horizontalArrangement = TastyHorizontalArrangement.SpacedBy(6),
                            children = listOf(
                                TastyIcon(icon = TastyMapIcons.STAR, color = palette.ratingTextColor, sizePx = 16),
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
                            verticalAlignment = TastyVerticalAlignment.CENTER,
                            horizontalArrangement = TastyHorizontalArrangement.SpacedBy(6),
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
            modifier = TastyModifier().fillMaxWidth(),
            verticalAlignment = TastyVerticalAlignment.CENTER,
            horizontalArrangement = TastyHorizontalArrangement.SpacedBy(8),
            children = listOf(
                TastyIcon(icon = TastyMapIcons.LOCATION, color = palette.primaryColor, sizePx = 18),
                TastyText(text = restaurant.address, style = TastyTextStyle.BODY, color = palette.subtitleColor)
            )
        ),

        TastyDivider(
            modifier = TastyModifier()
                .padding(top = 4, bottom = 4),
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
                            verticalAlignment = TastyVerticalAlignment.CENTER,
                            children = listOf(
                                TastyText(
                                    modifier = TastyModifier().weight(1f),
                                    text = "Ahmet Yılmaz",
                                    style = TastyTextStyle.BODY,
                                    color = palette.titleColor
                                ),
                                TastyRow(
                                    verticalAlignment = TastyVerticalAlignment.CENTER,
                                    horizontalArrangement = TastyHorizontalArrangement.SpacedBy(4),
                                    children = listOf(
                                        TastyIcon(icon = TastyMapIcons.STAR, color = "#F59E0B", sizePx = 12),
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
    )
    )
}


fun buildChaosTestedSheetUI(): TastyView {
    return TastyColumn(
        modifier = TastyModifier()
            .fillMaxWidth()
            .height(600)
            .background("#FFFFFF")
            .padding(16),
        verticalArrangement = TastyVerticalArrangement.SpacedBy(12), // CRITICAL TEST: Hem SpacedBy kullanıp hem de içeride asimetrik Spacer ekleyerek boşluk motorunu şaşırtıyoruz.
        horizontalAlignment = TastyHorizontalAlignment.CENTER,
        children = listOf(

            // -------------------------------------------------------------
            // TEST HARNES 1: MODIFIER ZİNCİRLEME SIRASI & ARKA PLAN EZME TESTİ
            // -------------------------------------------------------------
            TastyBox(
                modifier = TastyModifier()
                    .padding(top = 8)
                    .background("#E0E0E0") // CRITICAL TEST: Padding'den sonra gelen arka plan çizim sınırlarını taşıyacak mı?
                    .borderRadius(4)
                    .width(40)
                    .height(6),
                contentAlignment = TastyBoxAlignment.CENTER,
                children = emptyList()
            ),

            // -------------------------------------------------------------
            // TEST HARNES 2: WEIGHT FAŞİZANLIĞI & AYNI ANDA MAX WIDTH KULLANIMI
            // -------------------------------------------------------------
            TastyRow(
                modifier = TastyModifier().fillMaxWidth(),
                horizontalArrangement = TastyHorizontalArrangement.SpaceBetween,
                verticalAlignment = TastyVerticalAlignment.CENTER,
                children = listOf(
                    TastyBox(
                        modifier = TastyModifier()
                            .weight(0.7f) // CRITICAL TEST: Hem weight alıp hem fillMaxWidth diyerek ölçüm motorunun (MeasurePass) kafasını karıştırıyoruz.
                            .fillMaxWidth()
                            .background("#FF0000")
                            .padding(8),
                        contentAlignment = TastyBoxAlignment.CENTER_START,
                        children = emptyList()
                    ),
                    TastySpacer(
                        modifier = TastyModifier().width(16) // CRITICAL TEST: Space_Between + Weight + Sabit Spacer üçlüsü genişlik dağılımını kırabilir.
                    ),
                    TastyBox(
                        modifier = TastyModifier()
                            .weight(0.3f)
                            .height(40)
                            .background("#00FF00"),
                        contentAlignment = TastyBoxAlignment.CENTER_END,
                        children = emptyList()
                    )
                )
            ),

            // -------------------------------------------------------------
            // TEST HARNES 3: ASİMETRİK PADDING VE SPACER ÇATIŞMASI
            // -------------------------------------------------------------
            TastySpacer(
                modifier = TastyModifier()
                    .height(24)
                    .paddingBottom(16) // CRITICAL TEST: Spacer bileşenine asimetrik padding verilmesi boşluk hesaplamasını bozmalıdır.
            ),

            // -------------------------------------------------------------
            // TEST HARNES 4: HAYALET BOŞLUK & ZIT HİZALAMA ÇATIŞMASI (HAYALET AVINGI)
            // -------------------------------------------------------------
            TastyBox(
                modifier = TastyModifier()
                    .fillMaxWidth()
                    .height(120)
                    .background("#F5F5F5"),
                contentAlignment = TastyBoxAlignment.TOP_END, // Üst-Sağ hizalama
                children = listOf(
                    TastyRow(
                        modifier = TastyModifier()
                            .fillMaxWidth()
                            .height(60)
                            .weight(1.0f), // CRITICAL TEST: Box içindeki tek elemana weight atayarak sonsuz döngü arıyoruz.
                        horizontalArrangement = TastyHorizontalArrangement.Start,
                        verticalAlignment = TastyVerticalAlignment.BOTTOM, // İçerideki eleman en alta zorlanıyor (Zıt Hizalama çatışması)
                        children = listOf(
                            TastyBox(
                                modifier = TastyModifier()
                                    .width(50)
                                    .height(50)
                                    .background("#0000FF")
                                    .paddingBottom(20), // CRITICAL TEST: Bottom hizalı Row içindeki elemana alt padding verilerek taşıma (overflow) testi yapılıyor.
                                contentAlignment = TastyBoxAlignment.CENTER,
                                children = emptyList()
                            )
                        )
                    )
                )
            ),

            // -------------------------------------------------------------
            // TEST HARNES 5: AGRESİF ZİNCİRLEME VE ÖLÇEKLENDİRME LİMİTLERİ
            // -------------------------------------------------------------
            TastyRow(
                modifier = TastyModifier()
                    .fillMaxWidth()
                    .padding(4),
                horizontalArrangement = TastyHorizontalArrangement.SpaceEvenly,
                verticalAlignment = TastyVerticalAlignment.BOTTOM,
                children = listOf(
                    TastyBox(
                        modifier = TastyModifier()
                            .weight(0.5f)
                            .borderRadius(12)
                            .background("#FF00FF")
                            .padding(top = 50), // CRITICAL TEST: Yüksek padding ile Row yüksekliğini manipüle etme.
                        contentAlignment = TastyBoxAlignment.CENTER,
                        children = emptyList()
                    ),
                    TastyBox(
                        modifier = TastyModifier()
                            .weight(0.5f)
                            .height(80) // CRITICAL TEST: Yanındaki eleman padding ile büyürken, bu eleman sabit height ve weight ile sıkıştırılıyor.
                            .background("#FFFF00"),
                        contentAlignment = TastyBoxAlignment.BOTTOM_START,
                        children = emptyList()
                    )
                )
            )
        )
    )
}


fun buildChaosTestedSheetUI_V2(
    restaurantName: String = "Gürme Lezzetler Kebap & Lahmacun Sarayı", // Uzun text ile overflow zorlaması
    itemCount: Int = 12,
    totalPrice: String = "1450.50 TL",
    deliveryAddress: String = "Yazır Mah. Dr. Sıddık Öz Meram/Konya, Kule Site Karşısı No:42 Daire:10" // Çok satırlı adres stretch testi
): TastyView {
    return TastyColumn(
        modifier = TastyModifier()
            .fillMaxWidth()
            .height(600)
            .background("#FFFFFF")
            .padding(16),
        verticalArrangement = TastyVerticalArrangement.SpacedBy(12), // CRITICAL TEST: İçerideki kontrolsüz asimetrik spacer'lar ve dynamic row'lar ile SpacedBy motorunun toplam height hesaplamasını çökertmeye çalışıyoruz.
        horizontalAlignment = TastyHorizontalAlignment.CENTER,
        children = listOf(

            // -------------------------------------------------------------
            // TEST HARNES 1: SÜRÜKLEME ÇUBUĞU (MODIFIER ZİNCİRLEME SIRASI)
            // -------------------------------------------------------------
            TastyBox(
                modifier = TastyModifier()
                    .padding(top = 8)
                    .background("#CCCCCC") // CRITICAL TEST: Önce padding verip sonra background çizdirerek native view bounding-box sınırlarını test ediyoruz.
                    .borderRadius(4)
                    .width(40)
                    .height(6),
                contentAlignment = TastyBoxAlignment.CENTER,
                children = emptyList()
            ),

            // -------------------------------------------------------------
            // TEST HARNES 2: RESTORAN BAŞLIĞI VE KAPATMA BUTONU (WEIGHT FAŞİZANLIĞI)
            // -------------------------------------------------------------
            TastyRow(
                modifier = TastyModifier().fillMaxWidth(),
                horizontalArrangement = TastyHorizontalArrangement.SpaceBetween,
                verticalAlignment = TastyVerticalAlignment.CENTER,
                children = listOf(
                    // Restoran Adı Alanı (Büyük veri, ezici weight)
                    TastyBox(
                        modifier = TastyModifier()
                            .weight(0.8f) // CRITICAL TEST: Hem weight verip hem fillMaxWidth zorlaması yapıyoruz. Uzun restoran isminde ölçüm motoru ne yapacak?
                            .fillMaxWidth()
                            .background("#FFF9E6") // Hafif sarımsı restoran teması background'ı
                            .padding(8),
                        contentAlignment = TastyBoxAlignment.CENTER_START,
                        children = emptyList() // Not: Gerçek text motorun tarafından render edilecek varsayımıyla sarmalayıcı test ediliyor.
                    ),
                    TastySpacer(
                        modifier = TastyModifier().width(12) // CRITICAL TEST: SPACE_BETWEEN + Weight varken araya sabit genişlikte Spacer sokarak layout'u sıkıştırıyoruz.
                    ),
                    // Kapatma Icon Box'ı
                    TastyBox(
                        modifier = TastyModifier()
                            .weight(0.2f)
                            .height(40)
                            .background("#F5F5F5"),
                        contentAlignment = TastyBoxAlignment.CENTER,
                        children = emptyList()
                    )
                )
            ),

            // -------------------------------------------------------------
            // TEST HARNES 3: ASİMETRİK BOŞLUK ÇATIŞMASI
            // -------------------------------------------------------------
            TastySpacer(
                modifier = TastyModifier()
                    .height(16)
                    .paddingBottom(24) // CRITICAL TEST: Spacer bileşeninin kendisine alt padding vererek dikeyde çift katmanlı hayalet boşluk yaratma denemesi.
            ),

            // -------------------------------------------------------------
            // TEST HARNES 4: TESLİMAT ADRESİ PANELI (ZIT HİZALAMA VE HAYALET BOŞLUK)
            // -------------------------------------------------------------
            TastyBox(
                modifier = TastyModifier()
                    .fillMaxWidth()
                    .height(110)
                    .background("#FAFAFA"), // Kart arka planı
                contentAlignment = TastyBoxAlignment.TOP_END, // Üst-Sağ hizalama (Parent)
                children = listOf(
                    TastyRow(
                        modifier = TastyModifier()
                            .fillMaxWidth()
                            .height(55)
                            .weight(1.0f), // CRITICAL TEST: Box içindeki tek çocuğa weight atayarak sonsuz ölçüm döngüsü (infinite loop) tetikleme.
                        horizontalArrangement = TastyHorizontalArrangement.Start,
                        verticalAlignment = TastyVerticalAlignment.BOTTOM, // İçerideki eleman en alta zorlanıyor (Zıt Hizalama Çatışması)
                        children = listOf(
                            // Adres Detay Kutusu
                            TastyBox(
                                modifier = TastyModifier()
                                    .width(280) // Sabit genişlik ile çok uzun adres verisinin taşma (overflow) sınır testi
                                    .height(45)
                                    .background("#EAEAEA")
                                    .paddingBottom(15), // CRITICAL TEST: BOTTOM hizalı Row'da elemana alt padding vererek içeriği kırpma/taşırma zorlaması.
                                contentAlignment = TastyBoxAlignment.CENTER_START,
                                children = emptyList()
                            )
                        )
                    )
                )
            ),

            // -------------------------------------------------------------
            // TEST HARNES 5: SİPARİŞ ÖZETİ VE SATIN AL BUTONU (AGRESİF ÖLÇEKLENDİRME)
            // -------------------------------------------------------------
            TastyRow(
                modifier = TastyModifier()
                    .fillMaxWidth()
                    .padding(6),
                horizontalArrangement = TastyHorizontalArrangement.SpaceEvenly,
                verticalAlignment = TastyVerticalAlignment.BOTTOM,
                children = listOf(
                    // Sol Taraf: Toplam Fiyat ve Ürün Adedi Bilgisi Paneli
                    TastyBox(
                        modifier = TastyModifier()
                            .weight(0.4f) // Alanın %40'ı
                            .borderRadius(8)
                            .background("#F0FDF4") // Yeşilimsi sepet arka planı
                            .padding(top = 40), // CRITICAL TEST: Aşırı yüksek üst padding uygulayarak Row'un genel yüksekliğini manipüle edip yanındaki elemanı germe.
                        contentAlignment = TastyBoxAlignment.CENTER,
                        children = emptyList()
                    ),
                    // Sağ Taraf: "Siparişi Onayla" Buton Alanı
                    TastyBox(
                        modifier = TastyModifier()
                            .weight(0.6f) // Alanın %60'ı
                            .height(75) // CRITICAL TEST: Sol taraf padding ile doğal olarak büyürken, sağ tarafı sabit height (75) ve weight ile sıkıştırıp kırılma arıyoruz.
                            .background("#FF5A5F"), // Canlı buton rengi
                        contentAlignment = TastyBoxAlignment.CENTER,
                        children = emptyList()
                    )
                )
            )
        )
    )
}



fun buildChaosTestedSheetUI_V3(
    restaurantName: String = "Gürme Lezzetler Kebap & Lahmacun Sarayı",
    itemCount: Int = 12,
    totalPrice: String = "1450.50 TL",
    deliveryAddress: String = "Yazır Mah. Dr. Sıddık Öz Meram/Konya, Kule Site Karşısı No:42 Daire:10"
): TastyView {
    return TastyColumn(
        modifier = TastyModifier()
            .fillMaxWidth()
            .height(600)
            .background("#FFFFFF")
            .padding(16),
        verticalArrangement = TastyVerticalArrangement.SpacedBy(12),
        horizontalAlignment = TastyHorizontalAlignment.CENTER,
        children = listOf(

            // -------------------------------------------------------------
            // TEST HARNES 1: MODIFIER ZİNCİRLEME SIRASI (SÜRÜKLEME ÇUBUĞU)
            // -------------------------------------------------------------
            TastyBox(
                modifier = TastyModifier()
                    .padding(top = 8)
                    .background("#CCCCCC")
                    .borderRadius(4)
                    .width(40)
                    .height(6),
                contentAlignment = TastyBoxAlignment.CENTER,
                children = emptyList()
            ),

            // -------------------------------------------------------------
            // TEST HARNES 2: RESTORAN BAŞLIĞI (TEXT ÖLÇÜM & WEIGHT FAŞİZANLIĞI)
            // -------------------------------------------------------------
            TastyRow(
                modifier = TastyModifier().fillMaxWidth(),
                horizontalArrangement = TastyHorizontalArrangement.SpaceBetween,
                verticalAlignment = TastyVerticalAlignment.CENTER,
                children = listOf(
                    // Restoran Adı Alanı (Büyük veri, ezici weight ve dinamik metin)
                    TastyBox(
                        modifier = TastyModifier()
                            .weight(0.8f) // CRITICAL TEST: Hem weight verip hem fillMaxWidth zorluyoruz. İçerideki TastyText satır sarmalama (line-wrapping) yaparken motoru kilitleyecek mi?
                            .fillMaxWidth()
                            .background("#FFF9E6")
                            .padding(8),
                        contentAlignment = TastyBoxAlignment.CENTER_START,
                        children = listOf(
                            // Buraya doğrudan dışarıdan gelen dinamik string veriyi basıyoruz
                            TastyText(
                                text = restaurantName,
                                modifier = TastyModifier().fillMaxWidth() // Text'in kendisine de fillMaxWidth vererek çift yönlü zorlama yapıyoruz
                            )
                        )
                    ),
                    TastySpacer(
                        modifier = TastyModifier().width(12) // SPACE_BETWEEN + Weight varken araya sabit genişlikte Spacer
                    ),
                    // Kapatma Butonu Alanı
                    TastyBox(
                        modifier = TastyModifier()
                            .weight(0.2f)
                            .height(40)
                            .background("#F5F5F5"),
                        contentAlignment = TastyBoxAlignment.CENTER,
                        children = listOf(
                            TastyText(text = "X", modifier = TastyModifier())
                        )
                    )
                )
            ),

            // -------------------------------------------------------------
            // TEST HARNES 3: ASİMETRİK BOŞLUK ÇATIŞMASI
            // -------------------------------------------------------------
            TastySpacer(
                modifier = TastyModifier()
                    .height(16)
                    .paddingBottom(24) // Spacer bileşeninin kendisine alt padding vererek dikeyde çift katmanlı hayalet boşluk yaratma denemesi.
            ),

            // -------------------------------------------------------------
            // TEST HARNES 4: TESLİMAT ADRESİ (DİNAMİK ÇOK SATIRLI TEXT & OVERFLOW)
            // -------------------------------------------------------------
            TastyBox(
                modifier = TastyModifier()
                    .fillMaxWidth()
                    .height(110) // CRITICAL TEST: Sabit yüksekliğe sahip kutunun içine çok satırlı dinamik adres basıyoruz.
                    .background("#FAFAFA"),
                contentAlignment = TastyBoxAlignment.TOP_END,
                children = listOf(
                    TastyRow(
                        modifier = TastyModifier()
                            .fillMaxWidth()
                            .height(55)
                            .weight(1.0f), // Box içindeki tek çocuğa weight atayarak sonsuz ölçüm döngüsü tetikleme.
                        horizontalArrangement = TastyHorizontalArrangement.Start,
                        verticalAlignment = TastyVerticalAlignment.BOTTOM, // İçerideki eleman en alta zorlanıyor
                        children = listOf(
                            // Adres Detay Kutusu
                            TastyBox(
                                modifier = TastyModifier()
                                    .width(280) // Sabit genişlik ile çok uzun adres verisinin taşma (overflow) sınır testi
                                    .height(45)
                                    .background("#EAEAEA")
                                    .paddingBottom(15), // BOTTOM hizalı Row'da elemana alt padding vererek içeriği kırpma/taşırma zorlaması.
                                contentAlignment = TastyBoxAlignment.CENTER_START,
                                children = listOf(
                                    // Dinamik adres verisi
                                    TastyText(text = deliveryAddress, modifier = TastyModifier())
                                )
                            )
                        )
                    )
                )
            ),

            // -------------------------------------------------------------
            // TEST HARNES 5: SİPARİŞ ÖZETİ & SEPET DETAYI (AGRESİF ÖLÇEKLENDİRME)
            // -------------------------------------------------------------
            TastyRow(
                modifier = TastyModifier()
                    .fillMaxWidth()
                    .padding(6),
                horizontalArrangement = TastyHorizontalArrangement.SpaceEvenly,
                verticalAlignment = TastyVerticalAlignment.BOTTOM,
                children = listOf(
                    // Sol Taraf: Toplam Fiyat ve Ürün Adedi Bilgisi Paneli
                    TastyBox(
                        modifier = TastyModifier()
                            .weight(0.4f)
                            .borderRadius(8)
                            .background("#F0FDF4")
                            .padding(top = 40), // Aşırı yüksek üst padding uygulayarak Row'un genel yüksekliğini manipüle edip yanındaki elemanı germe.
                        contentAlignment = TastyBoxAlignment.CENTER,
                        children = listOf(
                            // Dinamik adet ve fiyat verisini tek metinde birleştirip zorluyoruz
                            TastyText(text = "$itemCount Adet - $totalPrice", modifier = TastyModifier())
                        )
                    ),
                    // Sağ Taraf: "Siparişi Onayla" Buton Alanı
                    TastyBox(
                        modifier = TastyModifier()
                            .weight(0.6f)
                            .height(75) // Sol taraf padding ile doğal olarak büyürken, sağ tarafı sabit height ve weight ile sıkıştırıp kırılma arıyoruz.
                            .background("#FF5A5F"),
                        contentAlignment = TastyBoxAlignment.CENTER,
                        children = listOf(
                            TastyText(text = "Siparişi Onayla", modifier = TastyModifier())
                        )
                    )
                )
            )
        )
    )
}

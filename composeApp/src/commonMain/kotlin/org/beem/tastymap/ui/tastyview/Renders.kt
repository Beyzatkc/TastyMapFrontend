package org.beem.tastymap.ui.tastyview

import org.beem.tastymap.data.model.Restaurant
import org.beem.tastymap.ui.tastyview.icons.TastyMapIcon
import org.beem.tastymap.ui.theme.TastyMapSheetPalette
import org.beem.tastymap.ui.map.bottomsheet.RestaurantAction
import org.beem.tastymap.ui.tastyview.paginglist.TastyPagingController
import org.beem.tastymap.ui.tastyview.paginglist.TastyPagingList
import org.beem.tastymap.ui.tastyview.tastylayoutenums.TastyHorizontalArrangement
import org.beem.tastymap.ui.tastyview.tastylayoutenums.TastyVerticalAlignment
import org.beem.tastymap.ui.tastyview.tastylayoutenums.TastyVerticalArrangement
import org.beem.tastymap.ui.tastyview.test.Test
import org.beem.tastymap.ui.tastyview.test.TestService

fun TestItem(item: Test): TastyView{
    return TastyText(text = item.content, style = TastyTextStyle.BODY)
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


fun PagingTest_V2(
    restaurant: Restaurant,
    sheetState: TastyBottomSheetState,
    palette: TastyMapSheetPalette,
    onAction: (RestaurantAction) -> Unit
): TastyView {
    val testService = TestService()

    // 🚀 ADIM 1: En dışa StickyContainer'ı koyuyoruz. Telsiz odası en tepede kuruluyor!
    return TastyStickyContainer(
        modifier = TastyModifier().fillMaxWidth(),

        // Yukarı kayınca yapışacak o asil üst barımız
        stickyHeader = TastyRow(
            modifier = TastyModifier()
                .fillMaxWidth()
                .background(palette.backgroundColor)
                .padding(top = 12, bottom = 12, left = 16, right = 16),
            horizontalArrangement = TastyHorizontalArrangement.SpaceBetween,
            verticalAlignment = TastyVerticalAlignment.Center,
            children = listOf(
                TastyText(text = restaurant.name, style = TastyTextStyle.TITLE, color = palette.titleColor),
                TastyIconButton(
                    modifier = TastyModifier().size(32, 32).background(palette.closeButtonBackground).borderRadius(50),
                    iconHtml = "&times;",
                    backgroundColor = palette.closeButtonBackground,
                    iconColor = palette.closeButtonIconColor,
                    onClick = { sheetState.close() }
                )
            )
        ),

        // 🚀 ADIM 2: Kaydırılabilir içerik olarak DOĞRUDAN PagingList'i veriyoruz!
        scrollableContent = TastyPagingList<Test>(
            modifier = TastyModifier().fillMaxWidth(),
            controller = TastyPagingController(pageSize = 10) { page, pageSize ->
                testService.getTestList(page, pageSize)
            },
            // 🔥 KRİTİK DOKUNUŞ: Restoranın o devasa detay sayfasını listenin İLK ELEMANI (Header) yapıyoruz!
            headerTemplate = {
                buildRestaurantSheetUI(
                    restaurant = restaurant,
                    sheetState = sheetState,
                    palette = palette,
                    onAction = onAction
                )
            },
            // API'den sayfa sayfa akan yeni test verileri bu detayların hemen altından tertemiz akacak
            itemTemplate = { test ->
                TestItem(item = test)
            },
            loadingTemplate = {
                TastyRow(
                    modifier = TastyModifier().fillMaxWidth().padding(16),
                    children = listOf(TastyText(text = "Yeni yorumlar yükleniyor, canını sıkma...", style = TastyTextStyle.BODY))
                )
            }
        )
    )
}
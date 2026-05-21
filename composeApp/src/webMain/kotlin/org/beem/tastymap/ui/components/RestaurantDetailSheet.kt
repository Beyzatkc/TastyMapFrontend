package org.beem.tastymap.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import org.beem.tastymap.data.model.Restaurant
import org.beem.tastymap.ui.map.bottomsheet.RestaurantAction
import org.beem.tastymap.ui.utils.rememberCentralizedSvg
import org.koin.core.component.getScopeId
import org.koin.core.component.getScopeName
import org.w3c.dom.HTMLElement
import org.w3c.dom.TouchEvent
import org.w3c.dom.events.MouseEvent
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.location

@Composable
actual fun RestaurantDetailSheet(
    restaurant: Restaurant,
    backgroundColor: String,
    primaryColor: String,
    cornerRadius: Int,
    sheetWidthPercentage: Int,
    onAction: (RestaurantAction) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetId = "tastymap-detail-sheet"

    val locationIcon = rememberCentralizedSvg("drawable/location.svg", "#f54254")

    SideEffect {
        var sheet = kotlinx.browser.document.getElementById(sheetId) as? HTMLElement
        if (sheet == null) {
            sheet = (kotlinx.browser.document.createElement("div") as HTMLElement).apply {
                id = sheetId
                setAttribute("style", """
                    position: absolute; 
                    bottom: 0;
                    left: 50%; 
                    transform: translate(-50%, 100%);
                    width: $sheetWidthPercentage%;
                    max-width: 500px;
                    background: $backgroundColor; 
                    border-radius: ${cornerRadius}px ${cornerRadius}px 0 0;
                    z-index: 1001; 
                    box-shadow: 0 -5px 20px rgba(0,0,0,0.1);
                    padding: 12px 24px 32px 24px;
                    box-sizing: border-box;
                    font-family: 'Inter', sans-serif;
                    transition: transform 0.4s cubic-bezier(0.22, 1, 0.36, 1);
                """.trimIndent())
                style.setProperty("transition", "transform 0.4s cubic-bezier(0.22, 1, 0.36, 1)")

                innerHTML = generateSheetContent(restaurant, primaryColor, locationIcon)
                kotlinx.browser.window.requestAnimationFrame {
                    style.transform = "translate(-50%, 0)"
                }
                kotlinx.browser.document.body?.appendChild(this)
            }
            bindEvents(sheet, onAction, onDismiss)
        }
        else {
            sheet.style.transform = "translate(-50%, 100%)"
            kotlinx.browser.window.setTimeout({
                sheet.innerHTML = generateSheetContent(restaurant, primaryColor, locationIcon)
                bindEvents(sheet, onAction, onDismiss)

                kotlinx.browser.window.requestAnimationFrame {
                    sheet.style.transform = "translate(-50%, 0)"
                }
                null
            }, 250)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            kotlinx.browser.document.getElementById(sheetId)?.remove()
        }
    }
}


private fun generateSheetContent(
    restaurant: Restaurant,
    primaryColor: String,
    locationIcon: String
): String {
    val statusText = if (restaurant.status == "OPERATIONAL") "Açık" else "Kapalı"
    val statusColor = if (restaurant.status == "OPERATIONAL") "#10B981" else "#EF4444"

    return """
        <div id="sheet-drag-handle" style="
            width: 100%; height: 20px; display: flex; justify-content: center; 
            align-items: center; cursor: grab; margin-bottom: 4px;
        ">
            <div style="width: 44px; height: 5px; background: #E5E7EB; border-radius: 100px;"></div>
        </div>
        
        <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 14px;">
            <div style="flex: 1; padding-right: 12px;">
                <div style="display: flex; align-items: center; gap: 8px; margin-bottom: 6px;">
                    <span style="
                        background: ${primaryColor}15; color: $primaryColor; 
                        padding: 4px 10px; border-radius: 20px; font-size: 0.75rem; 
                        font-weight: 700; text-transform: uppercase; letter-spacing: 0.5px;
                    ">${restaurant.category ?: "Restoran"}</span>
                    
                    <div style="display: flex; align-items: center; gap: 4px;">
                        <span style="width: 6px; height: 6px; background-color: $statusColor; border-radius: 50%;"></span>
                        <span style="color: #6B7280; font-size: 0.8rem; font-weight: 500;">$statusText</span>
                    </div>
                </div>
                
                <h2 style="margin: 0; font-size: 1.45rem; font-weight: 800; color: #111827; letter-spacing: -0.3px; line-height: 1.2;">
                    ${restaurant.name}
                </h2>
            </div>
            
            <button id="close-btn" style="
                background: #F3F4F6; border: none; border-radius: 50%; 
                width: 36px; height: 36px; cursor: pointer; display: flex; 
                align-items: center; justify-content: center; color: #4B5563;
                font-size: 1.25rem; font-weight: bold; transition: background 0.2s;
            " onmouseover="this.style.background='#E5E7EB'" onmouseout="this.style.background='#F3F4F6'">&times;</button>
        </div>

        <div style="width: 100%; height: 1px; background-color: #F3F4F6; margin-bottom: 14px;"></div>

        <div style="display: flex; flex-direction: column; gap: 10px; margin-bottom: 22px;">
            <div style="display: flex; align-items: center; gap: 6px;">
                <div style="display: flex; align-items: center; background: #FFFBEB; border: 1px solid #FEF3C7; padding: 4px 8px; border-radius: 8px;">
                    <span style="color: #F59E0B; font-size: 0.95rem; margin-right: 4px;">⭐</span>
                    <span style="color: #92400E; font-weight: 700; font-size: 0.9rem;">${restaurant.rating}</span>
                </div>
                <span style="color: #6B7280; font-size: 0.85rem; font-weight: 500;">
                    (${restaurant.totalRatings} değerlendirme)
                </span>
            </div>
            
            <div style="display: flex; align-items: center; gap: 6px;">
                <div style="width: 18px; height: 18px; flex-shrink: 0; display: flex; align-items: center; justify-content: center;">
                    $locationIcon
                </div>
                <p style="margin: 0; color: #4B5563; font-size: 0.88rem; line-height: 1.4; font-weight: 400;">
                    ${restaurant.address}
                </p>
            </div>
        </div>

        <button id="dir-btn" style="
            width: 100%; padding: 15px; background: $primaryColor; color: white; 
            border: none; border-radius: 14px; cursor: pointer; font-weight: 700; 
            font-size: 1rem; display: flex; align-items: center; justify-content: center; 
            gap: 8px; box-shadow: 0 4px 12px ${primaryColor}40; transition: transform 0.1s, opacity 0.2s;
        " onmousedown="this.style.transform='scale(0.98)'" onmouseup="this.style.transform='scale(1)'" onmouseover="this.style.opacity='0.95'" onmouseout="this.style.opacity='1'">
            <span>🚀</span>
            <span>Yol Tarifi Al</span>
        </button>
    """.trimIndent()
}


private fun bindEvents(sheet: HTMLElement, onAction: (RestaurantAction) -> Unit, onDismiss: () -> Unit) {
    val dragHandle = kotlinx.browser.document.getElementById("sheet-drag-handle") as? HTMLElement

    // Sürükleme Durum Değişkenleri
    var isDragging = false
    var startY = 0.0
    var currentTranslateY = 0.0

    fun closeSheetWithAnimation() {
        sheet.style.setProperty("transition", "transform 0.3s ease-out")
        sheet.style.transform = "translate(-50%, 100%)"
        kotlinx.browser.window.setTimeout({
            onDismiss()
            null
        }, 300)
    }

    fun resetSheetPosition() {
        sheet.style.setProperty("transition", "transform 0.3s cubic-bezier(0.22, 1, 0.36, 1)")
        sheet.style.transform = "translate(-50%, 0px)"
    }

    // --- Drag Başlangıcı ---
    val onDragStart = { clientY: Double ->
        isDragging = true
        startY = clientY
        sheet.style.setProperty("transition", "none") // Sürüklerken gecikme olmasın diye animasyonu kapatıyoruz
        if (dragHandle != null) dragHandle.style.cursor = "grabbing"
    }

    // --- Sürükleme Anı ---
    val onDragMove = { clientY: Double ->
        if (isDragging) {
            val deltaY = clientY - startY
            if (deltaY >= 0) { // Sadece aşağı doğru sürüklemeye izin veriyoruz
                currentTranslateY = deltaY
                sheet.style.transform = "translate(-50%, ${currentTranslateY}px)"
            }
        }
    }

    // --- Drag Bitiş ---
    val onDragEnd = {
        if (isDragging) {
            isDragging = false
            if (dragHandle != null) dragHandle.style.cursor = "grab"

            // Eğer sheet boyunun %30'undan fazla aşağı çekildiyse kapat, yoksa geri yerine oturt
            val sheetHeight = sheet.offsetHeight.toDouble()
            if (currentTranslateY > sheetHeight * 0.3) {
                closeSheetWithAnimation()
            } else {
                resetSheetPosition()
            }
            currentTranslateY = 0.0
        }
    }

    dragHandle?.addEventListener("mousedown", { event ->
        event as MouseEvent
        onDragStart(event.clientY.toDouble())
    })

    kotlinx.browser.window.addEventListener("mousemove", { event ->
        event as MouseEvent
        onDragMove(event.clientY.toDouble())
    })

    kotlinx.browser.window.addEventListener("mouseup", { _ ->
        onDragEnd()
    })

    dragHandle?.addEventListener("touchstart", { event ->
        event as TouchEvent
        if (event.touches.length > 0) {
            onDragStart(event.touches.item(0)!!.clientY.toDouble())
        }
    })

    kotlinx.browser.window.addEventListener("touchmove", { event ->
        event as TouchEvent
        if (event.touches.length > 0) {
            onDragMove(event.touches.item(0)!!.clientY.toDouble())
        }
    })

    kotlinx.browser.window.addEventListener("touchend", { _ ->
        onDragEnd()
    })

    (kotlinx.browser.document.getElementById("close-btn") as? HTMLElement)?.onclick = {
        closeSheetWithAnimation()
    }

    (kotlinx.browser.document.getElementById("dir-btn") as? HTMLElement)?.onclick = {
        onAction(RestaurantAction.GetDirections)
    }
}
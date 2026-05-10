package org.beem.tastymap.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import org.beem.tastymap.data.model.Restaurant
import org.beem.tastymap.ui.map.bottomsheet.RestaurantAction
import org.w3c.dom.HTMLElement

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
                innerHTML = generateSheetContent(restaurant, primaryColor)
                bindEvents(this, onAction, onDismiss)
                kotlinx.browser.window.requestAnimationFrame {
                    style.transform = "translate(-50%, 0)"
                }
                kotlinx.browser.document.body?.appendChild(this)
            }
        }
        else {
            sheet.style.transform = "translate(-50%, 100%)"
            kotlinx.browser.window.setTimeout({
                sheet.innerHTML = generateSheetContent(restaurant, primaryColor)
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
    primaryColor: String
): String{
    return """
                <div style="display: flex; justify-content: space-between; align-items: start;">
                    <div>
                        <h2 style="margin: 0; font-size: 1.4rem; color: #1a1a1a;">${restaurant.name}</h2>
                        <p style="margin: 4px 0; color: #666; font-size: 0.9rem;">${restaurant.address}</p>
                        <div style="display: flex; align-items: center; margin-top: 8px;">
                            <span style="color: #FFB300; font-weight: bold;">⭐ ${restaurant.rating}</span>
                            <span style="color: #999; font-size: 0.8rem; margin-left: 5px;">(${restaurant.totalRatings} yorum)</span>
                        </div>
                    </div>
                    <button id="close-btn" style="background: #eee; border: none; border-radius: 50%; width: 32px; height: 32px; cursor: pointer;">&times;</button>
                </div>
                <button id="dir-btn" style="
                    width: 100%; margin-top: 20px; padding: 14px;
                    background: $primaryColor; color: white; border: none;
                    border-radius: 12px; cursor: pointer; font-weight: 600; font-size: 1rem;
                ">Yol Tarifi Al</button>
            """.trimIndent()
}

private fun bindEvents(sheet: HTMLElement, onAction: (RestaurantAction) -> Unit, onDismiss: () -> Unit) {
    (kotlinx.browser.document.getElementById("close-btn") as? HTMLElement)?.onclick = {
        sheet.style.transform = "translate(-50%, 100%)"
        kotlinx.browser.window.setTimeout({
            onDismiss()
            null
        }, 350)
    }

    (kotlinx.browser.document.getElementById("dir-btn") as? HTMLElement)?.onclick = {
        onAction(RestaurantAction.GetDirections)
    }
}
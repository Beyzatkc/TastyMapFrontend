package org.beem.tastymap.ui.detailsheet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import kotlinx.browser.document
import org.beem.tastymap.core.util.executeDelayed
import org.beem.tastymap.core.util.setStyleTransform
import org.beem.tastymap.data.model.Restaurant
import org.beem.tastymap.place.state.ReviewPagingState
import org.beem.tastymap.ui.detailsheet.components.createReviewCardElement

import org.w3c.dom.HTMLElement
import kotlin.js.ExperimentalWasmJsInterop

@OptIn(ExperimentalWasmJsInterop::class)
@Composable
actual fun TastyDetailSheet(
    restaurant: Restaurant,
    pagingState: ReviewPagingState,
    onLoadMoreReviews: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetId = "tastymap-premium-sheet"

    DisposableEffect(restaurant.id) {
        var container = document.getElementById(sheetId) as? HTMLElement

        if (container == null) {
            container = document.createElement("div") as HTMLElement
            container.id = sheetId
            container.setAttribute("style", "position: fixed; bottom: 0; left: 0; width: 100%; z-index: 10000; display: flex; justify-content: center; pointer-events: none;")
            document.body?.appendChild(container)
        }

        val isOperational = restaurant.status.uppercase() == "OPERATIONAL"
        val statusBg = if (isOperational) "#E8F5E9" else "#FFEBEE"
        val statusColor = if (isOperational) "#2E7D32" else "#C62828"
        val statusText = if (isOperational) "Açık" else "Kapalı"

        container.innerHTML = """
            <div id="tastymap-sheet-content" style="
                width: 100%;
                max-width: 520px;
                background-color: #FFFFFF;
                border-top-left-radius: 24px;
                border-top-right-radius: 24px;
                box-shadow: 0 -10px 40px rgba(0, 0, 0, 0.12);
                padding: 20px 24px 24px 24px;
                pointer-events: auto;
                transform: translateY(100%);
                transition: transform 0.35s cubic-bezier(0.32, 0.72, 0, 1);
                font-family: -apple-system, BlinkMacSystemFont, sans-serif;
                box-sizing: border-box;
                display: flex;
                flex-direction: column;
                max-height: 80vh;
            ">
                <div style="display: flex; justify-content: center; margin-bottom: 16px; flex-shrink: 0;">
                    <div style="width: 36px; height: 4px; background-color: #E0E0E0; border-radius: 2px;"></div>
                </div>

                <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 12px; flex-shrink: 0;">
                    <div style="flex: 1; padding-right: 12px;">
                        <h2 style="margin: 0 0 6px 0; font-size: 20px; font-weight: 700; color: #1A1A1A; line-height: 1.3;">${restaurant.name}</h2>
                        <div style="display: flex; gap: 8px; align-items: center;">
                            <span style="background-color: #E8EAF6; color: #3F51B5; padding: 4px 10px; border-radius: 6px; font-size: 12px; font-weight: 600;">${restaurant.category.ifBlank { "Restoran" }}</span>
                            <span style="background-color: $statusBg; color: $statusColor; padding: 4px 10px; border-radius: 6px; font-size: 12px; font-weight: 600;">$statusText</span>
                        </div>
                    </div>
                    <button id="sheet-close-btn" style="background: #F5F5F5; border: none; width: 32px; height: 32px; border-radius: 16px; font-size: 18px; cursor: pointer; color: #616161; display: flex; align-items: center; justify-content: center;">&times;</button>
                </div>

                <div style="height: 1px; background-color: #EEEEEE; margin: 12px 0; flex-shrink: 0;"></div>

                <div id="reviews-scroll-container" style="flex: 1; overflow-y: auto; padding-right: 4px; margin-bottom: 12px;">
                    <h3 style="margin: 0 0 12px 0; font-size: 15px; font-weight: 700; color: #1A1A1A;">Kullanıcı Yorumları</h3>
                    <div id="reviews-list-target"></div>
                    <div id="paging-status-target" style="text-align: center; padding: 10px 0;"></div>
                </div>
            </div>
        """.trimIndent()

        val sheetContent = document.getElementById("tastymap-sheet-content") as? HTMLElement

        executeDelayed(30) {
            sheetContent?.setStyleTransform("translateY(0)")
        }

        val closeBtn = document.getElementById("sheet-close-btn")
        closeBtn?.addEventListener("click", {
            sheetContent?.setStyleTransform("translateY(100%)")
            executeDelayed(300) {
                onDismiss()
            }
        })

        onDispose {
            val element = document.getElementById(sheetId)
            element?.parentNode?.removeChild(element)
        }
    }

    SideEffect {
        val listTarget = document.getElementById("reviews-list-target") as? HTMLElement
        val statusTarget = document.getElementById("paging-status-target") as? HTMLElement

        if (listTarget != null) {
            listTarget.innerHTML = ""
            if (pagingState.items.isEmpty() && !pagingState.isLoading) {
                listTarget.innerHTML = "<p style='color: #757575; font-size: 13px; margin: 0;'>Henüz yorum yapılmamış.</p>"
            } else {
                pagingState.items.forEach { review ->
                    val cardElement = createReviewCardElement(review)
                    listTarget.appendChild(cardElement)
                }
            }
        }

        if (statusTarget != null) {
            statusTarget.innerHTML = ""
            if (pagingState.isLoading) {
                statusTarget.innerHTML = "<span style='font-size: 13px; color: #757575;'>Yorumlar yükleniyor...</span>"
            } else if (!pagingState.isEndReached) {
                val loadMoreBtn = document.createElement("button") as HTMLElement
                loadMoreBtn.setAttribute("style", "background: none; border: none; color: #00008B; font-size: 13px; font-weight: 600; cursor: pointer; padding: 6px 12px;")
                loadMoreBtn.textContent = "Daha Fazla Yorum Yükle"
                loadMoreBtn.addEventListener("click", { onLoadMoreReviews() })
                statusTarget.appendChild(loadMoreBtn)
            }
        }
    }
}
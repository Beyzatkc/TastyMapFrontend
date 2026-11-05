package org.beem.tastymap.ui.detailsheet.components

import org.beem.tastymap.place.model.review.ReviewItem
import org.w3c.dom.HTMLElement
import kotlinx.browser.document

fun createReviewCardElement(review: ReviewItem): HTMLElement {
    val card = document.createElement("div") as HTMLElement

    card.setAttribute("style", """
        background-color: #F8F9FA;
        border-radius: 12px;
        padding: 14px 16px;
        margin-bottom: 12px;
        box-sizing: border-box;
        border: 1px solid #EDEFF1;
        font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
    """.trimIndent())

    val starSvg = """
        <svg width="14" height="14" viewBox="0 0 24 24" fill="#FFC107" xmlns="http://www.w3.org/2000/svg" style="vertical-align: middle; margin-right: 2px;">
            <path d="M12 17.27L18.18 21L16.54 13.97L22 9.24L14.81 8.63L12 2L9.19 8.63L2 9.24L7.46 13.97L5.82 21L12 17.27Z"/>
        </svg>
    """.trimIndent()

    val reviewerName = review.name.ifBlank { "Anonim Kullanıcı" } ?: "Anonim Kullanıcı"
    val reviewComment = review.content.orEmpty().ifBlank { "Yorum belirtilmemiş." }
    val reviewRating = review.rating ?: 0.0

    card.innerHTML = """
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px;">
            <strong style="font-size: 14px; color: #1A1A1A; font-weight: 600;">$reviewerName</strong>
            <div style="display: flex; align-items: center; background-color: #FFF9E6; padding: 2px 6px; border-radius: 4px;">
                $starSvg
                <span style="font-size: 12px; font-weight: 700; color: #D4A373;">$reviewRating</span>
            </div>
        </div>
        <p style="margin: 0; font-size: 13px; color: #4A4A4A; line-height: 1.45; word-break: break-word;">
            $reviewComment
        </p>
    """.trimIndent()

    return card
}
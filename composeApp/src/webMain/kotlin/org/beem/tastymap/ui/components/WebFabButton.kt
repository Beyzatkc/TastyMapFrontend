package org.beem.tastymap.ui.components

import kotlinx.browser.document
import org.w3c.dom.HTMLElement

object WebFabButton {

    fun create(
        id: String = "tastymap-premium-fab",
        iconSvg: String = """<svg viewBox="0 0 24 24" width="20" height="20"><path fill="currentColor" d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z"/></svg>""",
        text: String? = null,
        fontFamily: String = "Roboto, -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif",
        backgroundColor: String = "#00008B",
        contentColor: String = "#FFFFFF",
        hoverBackgroundColor: String = "#0000CD",
        bottomPx: Int = 24,
        rightPx: Int = 24,
        onClick: () -> Unit
    ): HTMLElement {
        val fabBtn = document.createElement("button") as HTMLElement
        fabBtn.id = id

        val hasText = !text.isNullOrBlank()
        val widthStyle = if (hasText) "width: auto;" else "width: 56px;"
        val heightStyle = if (hasText) "height: 48px;" else "height: 56px;"
        val paddingStyle = if (hasText) "padding: 0px 20px;" else "padding: 0px;"
        val gapStyle = if (hasText) "gap: 8px;" else "gap: 0px;"

        fabBtn.setAttribute("style", """
            position: fixed;
            bottom: ${bottomPx}px;
            right: ${rightPx}px;
            z-index: 9999;
            
            display: inline-flex;
            align-items: center;
            justify-content: center;
            $widthStyle
            $heightStyle
            $paddingStyle
            $gapStyle
            
            background-color: $backgroundColor;
            color: $contentColor;
            
            border: none;
            border-radius: 16px;
            
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.25);
            cursor: pointer;
            
            font-family: $fontFamily;
            font-size: 14px;
            font-weight: 600;
            letter-spacing: 0.1px;
            white-space: nowrap;
            
            user-select: none;
            outline: none;
            transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
        """.trimIndent())

        val textSpanHtml = if (hasText) """<span style="line-height: 1;">$text</span>""" else ""

        fabBtn.innerHTML = """
            <span style="display: inline-flex; align-items: center; justify-content: center; color: $contentColor;">
                $iconSvg
            </span>
            $textSpanHtml
        """.trimIndent()

        fabBtn.addEventListener("click", { onClick() })

        fabBtn.addEventListener("mouseover", {
            fabBtn.style.backgroundColor = hoverBackgroundColor
            fabBtn.style.boxShadow = "0 6px 16px rgba(0, 0, 0, 0.35)"
            fabBtn.style.transform = "translateY(-1px)"
        })

        fabBtn.addEventListener("mouseout", {
            fabBtn.style.backgroundColor = backgroundColor
            fabBtn.style.boxShadow = "0 4px 12px rgba(0, 0, 0, 0.25)"
            fabBtn.style.transform = "translateY(0px)"
        })

        fabBtn.addEventListener("mousedown", {
            fabBtn.style.transform = "scale(0.95)"
        })

        fabBtn.addEventListener("mouseup", {
            fabBtn.style.transform = "scale(1)"
        })

        return fabBtn
    }

    fun remove(id: String = "tastymap-premium-fab") {
        val element = document.getElementById(id)
        element?.parentNode?.removeChild(element)
    }
}
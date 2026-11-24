package org.beem.tastymap.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.PointerIcon
import kotlinx.browser.document

private const val CURSOR_STYLE_ID = "tasty-dynamic-cursor-style"


private fun Color.toUrlHex(): String {
    val rgb = this.toArgb() and 0xFFFFFF
    return "%23" + rgb.toString(16).padStart(6, '0').uppercase()
}

actual fun createHighContrastTextPointerIcon(
    fillColor: Color,
    strokeColor: Color
): PointerIcon {
    val fillHex = fillColor.toUrlHex()
    val strokeHex = strokeColor.toUrlHex()

    // Zarif, ince (1px gövdeli) native görünümlü I-beam SVG (hotspot tam merkez: 8 9)
    val rawSvg = "<svg xmlns='http://www.w3.org/2000/svg' width='16' height='18' viewBox='0 0 16 18'>" +
            "<path d='M5.5 2h5v1.2H8.6v11.6h1.9V16h-5v-1.2h1.9V3.2H5.5V2z' " +
            "fill='$fillHex' stroke='$strokeHex' stroke-width='0.8' paint-order='stroke'/>" +
            "</svg>"

    val encodedSvg = rawSvg
        .replace("<", "%3C")
        .replace(">", "%3E")
        .replace("\"", "%22")
        .replace(" ", "%20")

    val svgCursor = "url(\"data:image/svg+xml,$encodedSvg\") 8 9, text"

    applyDynamicWebTextCursor(svgCursor)

    return PointerIcon.Text
}

private fun applyDynamicWebTextCursor(svgCursorValue: String) {
    val cssRule = """
        *[style*="cursor: text"],
        *[style*="cursor:text"] {
            cursor: $svgCursorValue !important;
        }
    """.trimIndent()

    // 1. Compose'un canvas'ı yerleştirdiği Shadow DOM içine enjekte et
    val shadowRoot = document.body?.shadowRoot
    if (shadowRoot != null) {
        val shadowStyle = shadowRoot.getElementById(CURSOR_STYLE_ID)
            ?: document.createElement("style").also {
                it.id = CURSOR_STYLE_ID
                shadowRoot.appendChild(it)
            }
        shadowStyle.textContent = cssRule
    }

    // 2. Fallback olarak ana document.head içine de ekle
    val head = document.head ?: return
    val headStyle = document.getElementById(CURSOR_STYLE_ID)
        ?: document.createElement("style").also {
            it.id = CURSOR_STYLE_ID
            head.appendChild(it)
        }
    headStyle.textContent = cssRule
}
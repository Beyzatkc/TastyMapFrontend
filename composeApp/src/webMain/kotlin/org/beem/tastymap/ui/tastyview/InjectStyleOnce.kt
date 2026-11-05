package org.beem.tastymap.ui.tastyview

import kotlinx.browser.document

private var isScrollbarStyleInjected = false

fun injectScrollbarStyleOnce() {
    if (isScrollbarStyleInjected) return
    try {
        val head = document.head ?: return
        val styleElement = document.createElement("style")
        styleElement.textContent = """
            .no-scrollbar::-webkit-scrollbar { display: none; }
            .no-scrollbar { -ms-overflow-style: none; scrollbar-width: none; }
        """.trimIndent()
        head.appendChild(styleElement)
        isScrollbarStyleInjected = true
    } catch (_: Exception) {
    }
}
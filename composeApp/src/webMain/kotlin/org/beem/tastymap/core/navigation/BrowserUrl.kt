package org.beem.tastymap.core.navigation

import kotlinx.browser.window

private var previousHash: String = ""

actual fun openPostUrl() {
    previousHash = window.location.hash
    window.history.replaceState(
        window.history.state,
        "",
        "#post"
    )
}

actual fun closePostUrl() {
    window.history.replaceState(
        window.history.state,
        "",
        previousHash.ifBlank { "#profile" }
    )
    previousHash = ""
}
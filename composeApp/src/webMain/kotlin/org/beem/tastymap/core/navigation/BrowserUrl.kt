package org.beem.tastymap.core.navigation

import kotlinx.browser.window

private var previousHash: String = ""

actual fun openPostUrl() {
    if (window.location.hash != "#post") {
        previousHash = window.location.hash
    }
    window.history.replaceState(
        window.history.state,
        "",
        "#post"
    )
}

actual fun closePostUrl() {
    if (previousHash.isNotBlank()) {
        window.history.replaceState(
            window.history.state,
            "",
            previousHash
        )
        previousHash = ""
    }
}
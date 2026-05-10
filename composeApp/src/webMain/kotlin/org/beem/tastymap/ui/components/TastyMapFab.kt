package org.beem.tastymap.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.WebElementView
import org.w3c.dom.HTMLButtonElement

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun TastyMapFab(
    onClick: () -> Unit,
    modifier: Modifier,
    backgroundColor: String,
    iconColor: String,
    zIndex: Float,
    icon: TastyIcon
) {
    val fabId = "tastymap-fab-button"
    val iconChar = icon.getWebLabel()

    SideEffect {
        var fab = kotlinx.browser.document.getElementById(fabId) as? org.w3c.dom.HTMLButtonElement
        if (fab == null) {
            fab = kotlinx.browser.document.createElement("button") as HTMLButtonElement
            fab.id = fabId
            kotlinx.browser.document.body?.appendChild(fab)
        }
        fab.apply {
            innerText = iconChar
            style.apply {
                position = "absolute"
                bottom = "24px"
                right = "24px"
                width = "56px"
                height = "56px"
                borderRadius = "50%"
                border = "none"
                fontSize = "24px"
                cursor = "pointer"
                className = "tastymap-fab"
                this.backgroundColor = backgroundColor
                this.color = iconColor
                this.zIndex = zIndex.toInt().toString()
                this.boxShadow = "0 4px 10px rgba(0,0,0,0.3)"
                innerHTML = "<span>$iconChar</span>"
            }
            onclick = { onClick() }
        }
    }
}
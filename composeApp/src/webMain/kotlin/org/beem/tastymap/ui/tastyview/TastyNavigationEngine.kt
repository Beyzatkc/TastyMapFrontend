package org.beem.tastymap.ui.tastyview

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLElement

object TastyNavigationEngine {
    private val stack = mutableListOf<Pair<TastyNavTarget, HTMLElement>>()
    private val dismissCallbacks = mutableMapOf<String, () -> Unit>()

    init {
        window.onpopstate = { popTop() }
    }

    fun push(target: TastyNavTarget, onBindEvents: (HTMLElement) -> Unit, onDismiss: () -> Unit) {
        if (stack.lastOrNull()?.first?.key == target.key) return

        dismissCallbacks[target.key] = onDismiss

        if (target.displayMode == SheetDisplayMode.REPLACE && stack.isNotEmpty()) {
            val top = stack.removeAt(stack.lastIndex)
            val topElement = top.second
            topElement.style.transform = "translate(-50%, 100%)"
            topElement.style.opacity = "0"
            window.setTimeout({
                topElement.remove()
                dismissCallbacks[top.first.key]?.invoke() as JsAny?
            }, 250)
        }
        else if (target.displayMode == SheetDisplayMode.STACK && stack.isNotEmpty()) {
            val active = stack.last()
            active.first.onPushedBack(active.second)
        }

        val newElement = (document.createElement("div") as HTMLElement).apply {
            id = target.key
            className = "tasty-navigation-node"
            setAttribute("style", target.wrapperStyle)
            innerHTML = target.renderContent()
        }

        val baseZIndex = if (stack.isNotEmpty()) (stack.last().second.style.zIndex.toIntOrNull() ?: 1001) + 1 else 1001
        newElement.style.zIndex = baseZIndex.toString()

        document.body?.appendChild(newElement)
        onBindEvents(newElement) // Widget kendi iç tıklama/sürükleme olaylarını bağlar

        stack.add(Pair(target, newElement))
        window.history.pushState(null, "", "")

        window.requestAnimationFrame {
            newElement.style.transform = target.activeTransformStyle
        }
    }

    private fun popTop() {
        if (stack.isEmpty()) return
        val top = stack.removeAt(stack.lastIndex)
        val topElement = top.second

        topElement.style.opacity = "0"
        topElement.style.transform = "translate(-50%, 100%) scale(0.9)"

        window.setTimeout({
            topElement.remove()
            dismissCallbacks[top.first.key]?.invoke()

            if (stack.isNotEmpty()) {
                val active = stack.last()
                active.first.onTopCame(active.second)
            }
            null
        }, 300)
    }

    fun popSpecific(key: String) {
        val index = stack.indexOfFirst { it.first.key == key }
        if (index != -1 && index == stack.lastIndex) {
            window.history.back()
        } else if (index != -1) {
            val item = stack.removeAt(index)
            item.second.remove()
            dismissCallbacks[item.first.key]?.invoke()
        }
    }
}
package org.beem.tastymap.ui.tastyview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.MouseEvent
import kotlinx.browser.window

@Composable
actual fun TastyBottomSheet(
    sheetState: TastyBottomSheetState,
    displayMode: SheetDisplayMode,
    widthPercentage: Int,
    cornerRadius: Int,
    backgroundColor: String,
    onDismiss: () -> Unit,
    content: () -> TastyView
) {
    val sheetUniqueId = "tasty-sheet-${sheetState.hashCode()}"
    val dragHandleColor = "#4B5563"
    val builtUI = content()

    SideEffect {
        val sheetTarget = object: TastyNavTarget {
            override val key = sheetUniqueId
            override val displayMode = displayMode

            override val wrapperStyle: String = """
                position: absolute; bottom: 0; left: 50%; 
                transform: translate(-50%, 100%); width: $widthPercentage%; max-width: 500px;
                background: $backgroundColor; border-radius: ${cornerRadius}px ${cornerRadius}px 0 0;
                box-shadow: 0 -5px 25px rgba(0,0,0,0.15); padding: 8px 24px 32px 24px; box-sizing: border-box;
                transition: transform 0.4s cubic-bezier(0.22, 1, 0.36, 1), filter 0.4s, opacity 0.4s;
            """.trimIndent()

            override val activeTransformStyle: String = "translate(-50%, 0px)"

            override fun renderContent(): String {
                val dragHandleHtml = """
                    <div id="drag-handle-$sheetUniqueId" style="width: 100%; height: 24px; display: flex; justify-content: center; align-items: center; cursor: grab;">
                        <div style="width: 44px; height: 5px; background: $dragHandleColor; border-radius: 100px;"></div>
                    </div>
                """.trimIndent()
                return dragHandleHtml + (builtUI.render() as String)
            }

            override fun onTopCame(domElement: Any) {
                (domElement as HTMLElement).style.transform = "translate(-50%, 0px) scale(1)"
                domElement.style.filter = "none"
                domElement.style.opacity = "1"
            }

            override fun onPushedBack(domElement: Any) {
                (domElement as HTMLElement).style.transform = "translate(-50%, 10px) scale(0.94)"
                domElement.style.filter = "blur(1px)"
                domElement.style.opacity = "0.6"
            }

        }
        sheetState.nativeCloseHandler = {
            TastyNavigationEngine.popSpecific(sheetUniqueId)
        }

        TastyNavigationEngine.push(
            target = sheetTarget,
            onBindEvents = { element ->
                bindSheetCoreEvents_V3(element, "drag-handle-$sheetUniqueId") {
                    TastyNavigationEngine.popSpecific(sheetUniqueId)
                }
            },
            onDismiss = onDismiss
        )
    }

    DisposableEffect(sheetState) {
        onDispose {
        }
    }
}


private fun bindSheetCoreEvents_V3(sheet: HTMLElement, handleId: String, closeAction: () -> Unit) {
    val dragHandle = kotlinx.browser.document.getElementById(handleId) as? HTMLElement
    var isDragging = false
    var startY = 0.0
    var currentTranslateY = 0.0

    val onDragStart = { clientY: Double ->
        isDragging = true
        startY = clientY
        sheet.style.setProperty("transition", "none")
        if (dragHandle != null) dragHandle.style.cursor = "grabbing"
    }

    val onDragMove = { clientY: Double ->
        if (isDragging) {
            val deltaY = clientY - startY
            if (deltaY >= 0) {
                currentTranslateY = deltaY
                sheet.style.transform = "translate(-50%, ${currentTranslateY}px)"
            }
        }
    }

    val onDragEnd = {
        if (isDragging) {
            isDragging = false
            if (dragHandle != null) dragHandle.style.cursor = "grab"
            val sheetHeight = sheet.offsetHeight.toDouble()
            if (currentTranslateY > sheetHeight * 0.3) {
                closeAction()
            } else {
                sheet.style.setProperty("transition", "transform 0.3s cubic-bezier(0.22, 1, 0.36, 1)")
                sheet.style.transform = "translate(-50%, 0px)"
            }
            currentTranslateY = 0.0
        }
    }

    dragHandle?.addEventListener("mousedown", { e -> onDragStart((e as MouseEvent).clientY.toDouble()) })
    window.addEventListener("mousemove", { e -> onDragMove((e as MouseEvent).clientY.toDouble()) })
    window.addEventListener("mouseup", { _ -> onDragEnd() })
}

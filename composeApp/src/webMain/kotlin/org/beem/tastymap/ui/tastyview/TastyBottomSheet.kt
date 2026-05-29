package org.beem.tastymap.ui.tastyview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import org.w3c.dom.HTMLElement
import org.w3c.dom.TouchEvent
import org.w3c.dom.events.MouseEvent
import kotlinx.browser.document
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
        val allSheets = document.querySelectorAll(".tasty-base-sheet")
        val activeSheet = if (allSheets.length > 0) allSheets.item(allSheets.length - 1) as? HTMLElement else null

        val dragHandleHtml = """
            <div id="drag-handle-$sheetUniqueId" style="
                width: 100%; height: 24px; display: flex; justify-content: center; 
                align-items: center; cursor: grab; user-select: none; margin-bottom: 4px;
            ">
                <div style="width: 44px; height: 5px; background: $dragHandleColor; border-radius: 100px;"></div>
            </div>
        """.trimIndent()

        val fullHtml = dragHandleHtml + (builtUI.render() as String)

        val closeWithAnimation = {
            val liveSheet = document.getElementById(sheetUniqueId) as? HTMLElement
            if (liveSheet != null) {
                liveSheet.style.setProperty("transition", "transform 0.3s cubic-bezier(0.36, 0.07, 0.19, 0.97)")
                liveSheet.style.transform = "translate(-50%, 100%)"

                window.setTimeout({
                    liveSheet.remove()
                    onDismiss()

                    val remainingSheets = document.querySelectorAll(".tasty-base-sheet")
                    if (remainingSheets.length > 0) {
                        val previousSheet = remainingSheets.item(remainingSheets.length - 1) as? HTMLElement
                        if (previousSheet != null) {
                            previousSheet.style.setProperty("transition", "transform 0.3s ease-out, filter 0.3s, opacity 0.3s")
                            previousSheet.style.transform = "translate(-50%, 0px) scale(1)"
                            previousSheet.style.filter = "none"
                            previousSheet.style.opacity = "1"
                        }
                    }
                    null
                }, 300)
            }
        }
        sheetState.nativeCloseHandler = closeWithAnimation

        fun createNewSheet(initialZIndex: Int): HTMLElement {
            return (document.createElement("div") as HTMLElement).apply {
                id = sheetUniqueId
                className = "tasty-base-sheet"
                setAttribute("style", """
                    position: absolute; bottom: 0; left: 50%; 
                    transform: translate(-50%, 100%); width: $widthPercentage%; max-width: 500px;
                    background: $backgroundColor; border-radius: ${cornerRadius}px ${cornerRadius}px 0 0;
                    z-index: $initialZIndex; box-shadow: 0 -5px 25px rgba(0,0,0,0.15);
                    padding: 8px 24px 32px 24px; box-sizing: border-box; font-family: 'Inter', sans-serif;
                """.trimIndent())
                style.setProperty("transition", "transform 0.4s cubic-bezier(0.22, 1, 0.36, 1), filter 0.4s, opacity 0.4s")
                innerHTML = fullHtml
            }
        }

        if (activeSheet == null) {
            println("activeSheet == null")
            val sheet = createNewSheet(1001)
            printSheetId(sheet.id)
            kotlinx.browser.document.body?.appendChild(sheet)
            bindSheetCoreEvents_V3(sheet, "drag-handle-$sheetUniqueId", closeWithAnimation)
            kotlinx.browser.window.requestAnimationFrame { sheet.style.transform = "translate(-50%, 0)" }
        }

        // DURUM 2: Ekranda sheet var ve kullanıcı BAŞKA bir aksiyon tetikledi
        else if (activeSheet.id != sheetUniqueId) {
            println("activeSheet.id != sheetUniqueId")
            printSheetId(activeSheet.id)

            if (displayMode == SheetDisplayMode.REPLACE) {
                println("Display Mode: REPLACE")
                // 🎯 REPLACE MODU: Eskisini aşağı kaydır, yok et, yenisini koy
                activeSheet.style.setProperty("transition", "transform 0.25s ease-in")
                activeSheet.style.transform = "translate(-50%, 100%)"

                kotlinx.browser.window.setTimeout({
                    activeSheet.remove()
                    val sheet = createNewSheet(1001)
                    kotlinx.browser.document.body?.appendChild(sheet)
                    bindSheetCoreEvents_V3(sheet, "drag-handle-$sheetUniqueId", closeWithAnimation)
                    kotlinx.browser.window.requestAnimationFrame { sheet.style.transform = "translate(-50%, 0)" }
                    null
                }, 250)

            } else {
                println("Display Mode: STACK")
                // 🎯 STACK MODU (EFSANE KISIM): Alttakini silme! Arkaya it, yenisini üste bindir
                activeSheet.style.setProperty("transition", "transform 0.4s cubic-bezier(0.22, 1, 0.36, 1), filter 0.4s, opacity 0.4s")
                // Alttakini hafif küçültüyoruz, biraz opacity verip arkaya gömüyoruz (iOS stili premium derinlik)
                activeSheet.style.transform = "translate(-50%, 10px) scale(0.94)"
                activeSheet.style.filter = "blur(1px)"
                activeSheet.style.opacity = "0.6"

                // Yeni katmanın z-index'ini bir tık yüksek yapıyoruz ki üste otursun
                val currentZIndex = (activeSheet.style.zIndex.toIntOrNull() ?: 1001) + 1
                val sheet = createNewSheet(currentZIndex)

                kotlinx.browser.document.body?.appendChild(sheet)
                bindSheetCoreEvents_V3(sheet, "drag-handle-$sheetUniqueId", closeWithAnimation)

                kotlinx.browser.window.requestAnimationFrame {
                    sheet.style.transform = "translate(-50%, 0)"
                }
            }
        }

        // DURUM 3: Aynı sheet güncelleniyor
        else {
            println("activeSheet.id == sheetUniqueId")
            printSheetId(activeSheet.id)
            activeSheet.innerHTML = fullHtml
            bindSheetCoreEvents_V3(activeSheet, "drag-handle-$sheetUniqueId", closeWithAnimation)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            kotlinx.browser.document.getElementById(sheetUniqueId)?.remove()
        }
    }
}

private fun bindSheetCoreEvents(sheet: HTMLElement, onDismiss: () -> Unit) {
    // Sürükleme handle elementini id üzerinden buluyoruz (Ağacımızda TastyDragHandle bu id'yi üretmeli)
    val dragHandle = kotlinx.browser.document.getElementById("sheet-drag-handle") as? HTMLElement

    var isDragging = false
    var startY = 0.0
    var currentTranslateY = 0.0

    fun closeSheetWithAnimation() {
        sheet.style.setProperty("transition", "transform 0.3s ease-out")
        sheet.style.transform = "translate(-50%, 100%)"
        kotlinx.browser.window.setTimeout({
            onDismiss()
            null
        }, 300)
    }

    fun resetSheetPosition() {
        sheet.style.setProperty("transition", "transform 0.3s cubic-bezier(0.22, 1, 0.36, 1)")
        sheet.style.transform = "translate(-50%, 0px)"
    }

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
                closeSheetWithAnimation()
            } else {
                resetSheetPosition()
            }
            currentTranslateY = 0.0
        }
    }

    // Fare Dinleyicileri
    dragHandle?.addEventListener("mousedown", { e -> onDragStart((e as MouseEvent).clientY.toDouble()) })
    kotlinx.browser.window.addEventListener("mousemove", { e -> onDragMove((e as MouseEvent).clientY.toDouble()) })
    kotlinx.browser.window.addEventListener("mouseup", { _ -> onDragEnd() })

    // Dokunmatik (Mobil) Dinleyicileri
    dragHandle?.addEventListener("touchstart", { e ->
        val touches = (e as TouchEvent).touches
        if (touches.length > 0) onDragStart(touches.item(0)!!.clientY.toDouble())
    })
    kotlinx.browser.window.addEventListener("touchmove", { e ->
        val touches = (e as TouchEvent).touches
        if (touches.length > 0) onDragMove(touches.item(0)!!.clientY.toDouble())
    })
    kotlinx.browser.window.addEventListener("touchend", { _ -> onDragEnd() })
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
                closeAction() // Drag bitince de ortak kapatma aksiyonunu çağırıyor
            } else {
                sheet.style.setProperty("transition", "transform 0.3s cubic-bezier(0.22, 1, 0.36, 1)")
                sheet.style.transform = "translate(-50%, 0px)"
            }
            currentTranslateY = 0.0
        }
    }

    // Event dinleyicileri (Mouse ve Touch) aynen bu dinamik handle elementine bağlanıyor...
    dragHandle?.addEventListener("mousedown", { e -> onDragStart((e as MouseEvent).clientY.toDouble()) })
    kotlinx.browser.window.addEventListener("mousemove", { e -> onDragMove((e as MouseEvent).clientY.toDouble()) })
    kotlinx.browser.window.addEventListener("mouseup", { _ -> onDragEnd() })
}

fun printSheetId(sheetId: String){
    println("Sheet ID: $sheetId")
}
package org.beem.tastymap.ui.tastyview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import org.w3c.dom.HTMLElement
import org.w3c.dom.TouchEvent
import org.w3c.dom.events.MouseEvent

@Composable
actual fun TastyBottomSheet(
    sheetState: TastyBottomSheetState,
    widthPercentage: Int,
    cornerRadius: Int,
    backgroundColor: String,
    onDismiss: () -> Unit,
    content: () -> TastyView
) {
    val sheetId = "tasty-sheet-${sheetState.hashCode()}"
    val builtUI = content()
    val dragHandleColor = "#CCCCCC"

    SideEffect {
        var sheet = kotlinx.browser.document.getElementById(sheetId) as? HTMLElement

        val dragHandleHtml = """
            <div id="drag-handle-$sheetId" style="
                width: 100%; height: 24px; display: flex; justify-content: center; 
                align-items: center; cursor: grab; user-select: none; margin-bottom: 4px;
            ">
                <div style="width: 44px; height: 5px; background: $dragHandleColor; border-radius: 100px;"></div>
            </div>
        """.trimIndent()

        val fullHtml = dragHandleHtml + (builtUI.render() as String)

        // 🎯 İŞTE EN ASİL KISIM: Animasyonlu kapatma fonksiyonunu yerel olarak yazıp,
        // dışarıdaki o jenerik Kotlin State nesnesinin kalbine enjekte ediyoruz!
        val closeWithAnimation = {
            val liveSheet = kotlinx.browser.document.getElementById(sheetId) as? HTMLElement
            if (liveSheet != null) {
                liveSheet.style.setProperty("transition", "transform 0.3s ease-out")
                liveSheet.style.transform = "translate(-50%, 100%)"
                kotlinx.browser.window.setTimeout({
                    onDismiss()
                    null
                }, 300)
            }
        }

        // Kütüphaneyi kullanan adam .close() dediği an bu yukarıdaki lambda çalışacak!
        sheetState.nativeCloseHandler = closeWithAnimation

        if (sheet == null) {
            sheet = (kotlinx.browser.document.createElement("div") as HTMLElement).apply {
                id = sheetId
                setAttribute("style", """
                    position: absolute; bottom: 0; left: 50%; 
                    transform: translate(-50%, 100%); width: $widthPercentage%; max-width: 500px;
                    background: $backgroundColor; border-radius: ${cornerRadius}px ${cornerRadius}px 0 0;
                    z-index: 1001; box-shadow: 0 -5px 20px rgba(0,0,0,0.1);
                    padding: 8px 24px 32px 24px; box-sizing: border-box; font-family: 'Inter', sans-serif;
                """.trimIndent())
                style.setProperty("transition", "transform 0.4s cubic-bezier(0.22, 1, 0.36, 1)")

                innerHTML = fullHtml

                kotlinx.browser.window.requestAnimationFrame {
                    style.transform = "translate(-50%, 0)"
                }
                kotlinx.browser.document.body?.appendChild(this)
            }
            // Sürükleme handle ID'sini de benzersiz gönderiyoruz
            bindSheetCoreEvents_V3(sheet, "drag-handle-$sheetId", closeWithAnimation)
        } else {
            sheet.innerHTML = fullHtml
            bindSheetCoreEvents_V3(sheet, "drag-handle-$sheetId", closeWithAnimation)
        }
    }

    DisposableEffect(sheetState) {
        onDispose {
            kotlinx.browser.document.getElementById(sheetId)?.remove()
            sheetState.nativeCloseHandler = null
        }
    }

    /*

    SideEffect {
        var sheet = kotlinx.browser.document.getElementById(sheetId) as? HTMLElement

        if (sheet == null) {
            sheet = (kotlinx.browser.document.createElement("div") as HTMLElement).apply {
                id = sheetId
                setAttribute("style", """
                    position: absolute; 
                    bottom: 0;
                    left: 50%; 
                    transform: translate(-50%, 100%);
                    width: $widthPercentage%;
                    max-width: 500px;
                    background: $backgroundColor; 
                    border-radius: ${cornerRadius}px ${cornerRadius}px 0 0;
                    z-index: 1001; 
                    box-shadow: 0 -5px 20px rgba(0,0,0,0.1);
                    padding: 12px 24px 32px 24px;
                    box-sizing: border-box;
                    font-family: 'Inter', sans-serif;
                """.trimIndent())
                style.setProperty("transition", "transform 0.4s cubic-bezier(0.22, 1, 0.36, 1)")

                innerHTML = builtUI.render() as String

                kotlinx.browser.window.requestAnimationFrame {
                    style.transform = "translate(-50%, 0)"
                }
                kotlinx.browser.document.body?.appendChild(this)
            }
            // Yaşayan mekanizmayı (Drag-drop/Scroll) bu jenerik konteynere bağlıyoruz!
            bindSheetCoreEvents(sheet, onDismiss)
        } else {
            // Eğer sheet zaten varsa sadece içeriğini tazele
            sheet.style.transform = "translate(-50%, 100%)"
            kotlinx.browser.window.setTimeout({
                sheet.innerHTML = builtUI.render() as String
                bindSheetCoreEvents(sheet, onDismiss)
                kotlinx.browser.window.requestAnimationFrame {
                    sheet.style.transform = "translate(-50%, 0)"
                }
                null
            }, 250)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            kotlinx.browser.document.getElementById(sheetId)?.remove()
        }
    }*/
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
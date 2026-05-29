package org.beem.tastymap.ui.tastyview

import kotlin.random.Random

actual class TastyButton actual constructor(
    private val text: String,
    private val icon: String?,
    private val backgroundColor: String,
    private val textColor: String,
    private val onClick: () -> Unit
) : TastyView {
    private val buttonId = "btn_${Random.nextInt(100000, 999999)}"

    actual override fun render(): Any {
        val iconHtml = if (icon != null) "<span>$icon</span>" else ""

        TastyButtonRegistry.register(buttonId, onClick)

        return """
            <button id="$buttonId" onclick="window.tastyTrigger('$buttonId')" style="
                width: 100%; padding: 15px; background: $backgroundColor; color: $textColor; 
                border: none; border-radius: 14px; cursor: pointer; font-weight: 700; 
                font-size: 1rem; display: flex; align-items: center; justify-content: center; 
                gap: 8px; box-shadow: 0 4px 12px ${backgroundColor}40; transition: transform 0.1s, opacity 0.2s;
            " onmousedown="this.style.transform='scale(0.98)'" onmouseup="this.style.transform='scale(1)'" onmouseover="this.style.opacity='0.95'" onmouseout="this.style.opacity='1'">
                $iconHtml
                <span>$text</span>
            </button>
        """.trimIndent()
    }
}
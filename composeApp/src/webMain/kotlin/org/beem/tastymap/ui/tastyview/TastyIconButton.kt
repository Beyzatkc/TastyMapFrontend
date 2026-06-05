package org.beem.tastymap.ui.tastyview

import kotlin.random.Random

actual class TastyIconButton actual constructor(
    private val iconHtml: String,
    private val backgroundColor: String,
    private val iconColor: String,
    private val onClick: () -> Unit
) : TastyView {

    private val buttonId = "icon_btn_${Random.nextInt(100000, 999999)}"

    actual override fun render(): TastyPlatformView {
        // Tıklama fonksiyonunu o kurduğumuz güvenli Kotlin haritamıza kaydediyoruz
        TastyButtonRegistry.register(buttonId, onClick)

        // Senin o şık &times; (çarpı) veya ikon butonunun CSS karşılığı
        return """
            <button id="$buttonId" 
                onclick="window.tastyTrigger('$buttonId')"
                style="
                    background: $backgroundColor; 
                    color: $iconColor; 
                    border: none; 
                    border-radius: 50%; 
                    width: 36px; 
                    height: 36px; 
                    cursor: pointer; 
                    display: flex; 
                    align-items: center; 
                    justify-content: center; 
                    font-size: 1.25rem; 
                    font-weight: bold; 
                    transition: background 0.2s, transform 0.1s;
                    box-sizing: border-box;
                    padding: 0;
                " 
                onmousedown="this.style.transform='scale(0.95)'" 
                onmouseup="this.style.transform='scale(1)'" 
                onmouseover="this.style.filter='brightness(0.9)'" 
                onmouseout="this.style.filter='none'">
                $iconHtml
            </button>
        """.trimIndent()
    }
}
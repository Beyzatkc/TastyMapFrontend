package org.beem.tastymap.ui.tastyview

actual class TastyCard actual constructor(
    private val backgroundColor: String,
    private val cornerRadius: Int,
    private val padding: Int,
    private val children: List<TastyView>
) : TastyView {

    actual override fun render(): Any {
        // Kartın içindeki tüm alt bileşenleri render edip tek bir string'de topluyoruz
        val childrenHtml = children.joinToString("") { it.render() as String }

        // Dışarıdan gelen parametrelerle tamamen dinamikleşen o şık kart gövdesi
        return """
            <div class="tasty-card" style="
                background: $backgroundColor; 
                border-radius: ${cornerRadius}px; 
                padding: ${padding}px;
                display: flex;
                flex-direction: column;
                gap: 8px;
                box-sizing: border-box;
                width: 100%;
            ">
                $childrenHtml
            </div>
        """.trimIndent()
    }
}
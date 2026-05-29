package org.beem.tastymap.ui.tastyview

actual class TastyLazyColumn actual constructor(
    private val key: String,
    private val items: List<TastyView>,
    private val onLoadMore: () -> Unit
) : TastyView {
    actual override fun render(): Any {
        val childrenHtml = items.joinToString("") { it.render() as String }
        return """
            <div id="$key" class="tasty-lazy-list" style="
                overflow-y: auto; 
                max-height: 300px; 
                display: flex; 
                flex-direction: column; 
                gap: 10px;
                -webkit-overflow-scrolling: touch;
            ">
                $childrenHtml
            </div>
        """.trimIndent()
    }
}
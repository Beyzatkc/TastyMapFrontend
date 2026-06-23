package org.beem.tastymap.ui.tastyview

import kotlinx.browser.document
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.beem.tastymap.ui.tastyview.contractual.TastyStickyScrollableContent
import org.beem.tastymap.ui.tastyview.to.initIntersectionObserverWasm

actual class TastyStickyContainer actual constructor(
    override val modifier: TastyModifier,
    private val stickyHeader: TastyView,
    private val scrollableContent: TastyStickyScrollableContent
): TastyView{

    private val uniqueTriggerId = "tasty-trigger-${hashCode()}"

    actual override fun render(): TastyPlatformView {

        injectUniqueIdToTrigger(scrollableContent)

        val headerHtml = stickyHeader.render()
        val contentHtml = scrollableContent.render()

        val html = """
            <div class="tasty-sticky-container" style=
                "position: relative;
                width: 100%;
                height: 100%;
                display: flex;
                flex-direction: column;
                box-sizing: border-box;
                overflow-y: auto;
                overflow-x: hidden;
                scrollbar-width: none;
                ${modifier.toCssStyle()}
            ">
                
                <div id="topbar-$uniqueTriggerId" style="
                    position: sticky; 
                    top: 0; 
                    z-index: 100; 
                    width: 100%; 
                    box-sizing: border-box;
                    margin-bottom: -60px;
                    transform: translateY(-10px);
                    opacity: 0; 
                    visibility: hidden; 
                    transition: opacity 0.25s ease, visibility 0.25s ease; 
                    pointer-events: none;
                ">
                    $headerHtml
                </div>

                <div style="flex-grow: 1; width: 100%; box-sizing: border-box;">
                    $contentHtml
                </div>

            </div>
        """.trimIndent()

        setupScrollObserverLazy()

        return html
    }

    private fun injectUniqueIdToTrigger(view: TastyView) {
        if (view is TastyColumn) {
            view.children.forEach { child ->
                if(child.modifier.isStickyTrigger){
                    child.modifier.setInternalId(uniqueTriggerId)
                }
            }
        }
    }


    private fun setupScrollObserverLazy() {
        MainScope().launch {
            kotlinx.coroutines.delay(100)

            initIntersectionObserverWasm(uniqueTriggerId, "topbar-$uniqueTriggerId")
        }
    }
}
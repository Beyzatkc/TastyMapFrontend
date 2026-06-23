package org.beem.tastymap.ui.tastyview

import org.beem.tastymap.ui.tastyview.contractual.TastyStickyScrollableContent

expect class TastyStickyContainer(
    modifier: TastyModifier,
    stickyHeader: TastyView,
    scrollableContent: TastyStickyScrollableContent
): TastyView {
    override fun render(): TastyPlatformView
}
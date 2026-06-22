package org.beem.tastymap.ui.tastyview.paginglist

import org.beem.tastymap.ui.tastyview.TastyModifier
import org.beem.tastymap.ui.tastyview.TastyPlatformView
import org.beem.tastymap.ui.tastyview.TastyView

class TastyPagingList<T>(
    override val modifier: TastyModifier = TastyModifier(),
    val controller: TastyPagingController<T>,
    val headerTemplate: (() -> TastyView)? = null,
    val itemTemplate: (item: T) -> TastyView,
    val loadingTemplate: () -> TastyView
): TastyView {
    override fun render(): TastyPlatformView {
        return platformRender(this)
    }

}

internal expect fun <T> platformRender(pagingList: TastyPagingList<T>): TastyPlatformView
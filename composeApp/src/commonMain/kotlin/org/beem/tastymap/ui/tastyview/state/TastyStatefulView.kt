package org.beem.tastymap.ui.tastyview.state

import org.beem.tastymap.ui.tastyview.TastyPlatformView
import org.beem.tastymap.ui.tastyview.TastyView

class TastyStatefulView<S>(
    private val state: TastyMutableState<S>,
    private val contentBuilder: (S) -> TastyView
) : TastyView {

    override fun render(): TastyPlatformView {
        return platformStatefulRender(state, contentBuilder)
    }
}
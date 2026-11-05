package org.beem.tastymap.ui.tastyview.state

import org.beem.tastymap.ui.tastyview.TastyPlatformView
import org.beem.tastymap.ui.tastyview.TastyView

internal expect fun <S> platformStatefulRender(
    state: TastyMutableState<S>,
    contentBuilder: (S) -> TastyView
): TastyPlatformView
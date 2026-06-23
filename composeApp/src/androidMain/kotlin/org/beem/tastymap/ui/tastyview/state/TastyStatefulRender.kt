package org.beem.tastymap.ui.tastyview.state

import androidx.compose.runtime.*
import org.beem.tastymap.ui.tastyview.TastyPlatformView
import org.beem.tastymap.ui.tastyview.TastyView

internal actual fun <S> platformStatefulRender(
    state: TastyMutableState<S>,
    contentBuilder: (S) -> TastyView
): TastyPlatformView {
    return TastyPlatformView {
        var localState by remember { mutableStateOf(state.value) }

        LaunchedEffect(state) {
            state.observe { newValue ->
                localState = newValue
            }
        }

        contentBuilder(localState).render().content()
    }
}
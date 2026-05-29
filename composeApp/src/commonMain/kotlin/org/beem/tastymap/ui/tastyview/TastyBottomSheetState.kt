package org.beem.tastymap.ui.tastyview

class TastyBottomSheetState {
    internal var nativeCloseHandler: (() -> Unit)? = null

    fun close() {
        nativeCloseHandler?.invoke()
    }
}
package org.beem.tastymap.ui.tastyview


expect class TastyIconButton(
    iconHtml: String,
    backgroundColor: String,
    iconColor: String,
    onClick: () -> Unit
) : TastyView {
    override fun render(): TastyPlatformView
}
package org.beem.tastymap.ui.tastyview


expect class TastyIconButton(
    modifier: TastyModifier = TastyModifier(),
    iconHtml: String,
    backgroundColor: String,
    iconColor: String,
    onClick: () -> Unit
) : TastyView {
    override fun render(): TastyPlatformView
}
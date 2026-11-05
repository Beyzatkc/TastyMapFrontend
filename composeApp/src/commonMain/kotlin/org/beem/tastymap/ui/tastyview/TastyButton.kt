package org.beem.tastymap.ui.tastyview

expect class TastyButton(
    text: String,
    icon: String? = null,
    backgroundColor: String,
    textColor: String = "#FFFFFF",
    onClick: () -> Unit
) : TastyView {
    override fun render(): TastyPlatformView
}
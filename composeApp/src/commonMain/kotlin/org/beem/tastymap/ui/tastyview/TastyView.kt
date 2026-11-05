package org.beem.tastymap.ui.tastyview


interface TastyView{
    val modifier: TastyModifier
        get() = TastyModifier()
    fun render(): TastyPlatformView
}
package org.beem.tastymap.ui.tastyview.tastylayoutenums

sealed class TastyVerticalArrangement {
    object Top : TastyVerticalArrangement()
    object Bottom : TastyVerticalArrangement()
    object Center : TastyVerticalArrangement()
    object SpaceBetween : TastyVerticalArrangement()
    object SpaceAround : TastyVerticalArrangement()
    object SpaceEvenly : TastyVerticalArrangement()

    data class SpacedBy(val spaceDp: Int) : TastyVerticalArrangement()
}
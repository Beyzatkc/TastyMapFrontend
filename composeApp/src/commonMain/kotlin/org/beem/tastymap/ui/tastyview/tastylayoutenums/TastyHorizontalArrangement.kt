package org.beem.tastymap.ui.tastyview.tastylayoutenums

sealed class TastyHorizontalArrangement {
    object Start : TastyHorizontalArrangement()
    object End : TastyHorizontalArrangement()
    object Center : TastyHorizontalArrangement()
    object SpaceBetween : TastyHorizontalArrangement()
    object SpaceAround : TastyHorizontalArrangement()
    object SpaceEvenly : TastyHorizontalArrangement()

    data class SpacedBy(val spaceDp: Int) : TastyHorizontalArrangement()
}
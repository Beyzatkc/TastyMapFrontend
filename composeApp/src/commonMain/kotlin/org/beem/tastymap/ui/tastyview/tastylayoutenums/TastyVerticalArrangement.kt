package org.beem.tastymap.ui.tastyview.tastylayoutenums

sealed class TastyVerticalArrangement {
    object TOP : TastyVerticalArrangement()
    object BOTTOM : TastyVerticalArrangement()
    object CENTER : TastyVerticalArrangement()
    object SPACE_BETWEEN : TastyVerticalArrangement()
    object SPACE_AROUND : TastyVerticalArrangement()
    object SPACE_EVENLY : TastyVerticalArrangement()

    data class SpacedBy(val spaceDp: Int) : TastyVerticalArrangement()
}
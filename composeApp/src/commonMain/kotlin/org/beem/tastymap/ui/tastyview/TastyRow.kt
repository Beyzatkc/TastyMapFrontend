package org.beem.tastymap.ui.tastyview

import androidx.compose.runtime.Composable

expect class TastyRow(
    modifier: TastyModifier = TastyModifier(),
    children: List<TastyView>
) : TastyView {
    override fun render(): TastyPlatformView
}
package org.beem.tastymap.ui.tastyview.to

import org.beem.tastymap.ui.tastyview.tastylayoutenums.TastyBoxAlignment
import org.beem.tastymap.ui.tastyview.tastylayoutenums.TastyHorizontalAlignment
import org.beem.tastymap.ui.tastyview.tastylayoutenums.TastyHorizontalArrangement
import org.beem.tastymap.ui.tastyview.tastylayoutenums.TastyVerticalAlignment
import org.beem.tastymap.ui.tastyview.tastylayoutenums.TastyVerticalArrangement

fun TastyHorizontalArrangement.toCssJustifyContent(): String {
    return when (this) {
        TastyHorizontalArrangement.Start -> "justify-content: flex-start;"
        TastyHorizontalArrangement.End -> "justify-content: flex-end;"
        TastyHorizontalArrangement.Center -> "justify-content: center;"
        TastyHorizontalArrangement.SpaceBetween -> "justify-content: space-between;"
        TastyHorizontalArrangement.SpaceAround -> "justify-content: space-around;"
        TastyHorizontalArrangement.SpaceEvenly -> "justify-content: space-evenly;"
        is TastyHorizontalArrangement.SpacedBy -> {
            "justify-content: flex-start; gap: ${this.spaceDp}px;"
        }
    }
}

fun TastyVerticalArrangement.toCssJustifyContent(): String {
    return when(this){
        TastyVerticalArrangement.Top -> "justify-content: flex-start;"
        TastyVerticalArrangement.Bottom -> "justify-content: flex-end;"
        TastyVerticalArrangement.Center -> "justify-content: center;"
        TastyVerticalArrangement.SpaceBetween -> "justify-content: space-between;"
        TastyVerticalArrangement.SpaceAround -> "justify-content: space-around;"
        TastyVerticalArrangement.SpaceEvenly -> "justify-content: space-evenly;"
        is TastyVerticalArrangement.SpacedBy -> {
            "justify-content: flex-start; gap: ${this.spaceDp}px;"
        }
    }
}

fun TastyVerticalAlignment.toCssAlignItems(): String {
    return when (this) {
        TastyVerticalAlignment.Top -> "align-items: flex-start;"
        TastyVerticalAlignment.Center -> "align-items: center;"
        TastyVerticalAlignment.Bottom -> "align-items: flex-end;"
    }
}

fun TastyHorizontalAlignment.toCssAlignItems(): String {
    return when(this){
        TastyHorizontalAlignment.Start -> "align-items: flex-start;"
        TastyHorizontalAlignment.Center -> "align-items: center;"
        TastyHorizontalAlignment.End -> "align-items: flex-end;"
    }
}

fun TastyBoxAlignment.toCssAlignItems(): String {
    return when (this) {
        TastyBoxAlignment.TopStart -> "justify-content: flex-start; align-items: flex-start;"
        TastyBoxAlignment.TopCenter -> "justify-content: center; align-items: flex-start;"
        TastyBoxAlignment.TopEnd -> "justify-content: flex-end; align-items: flex-start;"

        TastyBoxAlignment.CenterStart -> "justify-content: flex-start; align-items: center;"
        TastyBoxAlignment.Center -> "justify-content: center; align-items: center;"
        TastyBoxAlignment.CenterEnd -> "justify-content: flex-end; align-items: center;"

        TastyBoxAlignment.BottomStart -> "justify-content: flex-start; align-items: flex-end;"
        TastyBoxAlignment.BottomCenter -> "justify-content: center; align-items: flex-end;"
        TastyBoxAlignment.BottomEnd -> "justify-content: flex-end; align-items: flex-end;"
    }
}
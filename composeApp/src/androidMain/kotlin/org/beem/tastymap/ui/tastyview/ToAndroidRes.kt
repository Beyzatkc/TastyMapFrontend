package org.beem.tastymap.ui.tastyview

import org.beem.tastymap.composeapp.generated.resources.Res
import org.beem.tastymap.composeapp.generated.resources.location
import org.beem.tastymap.composeapp.generated.resources.star

import org.beem.tastymap.ui.tastyview.icons.TastyMapIcon
import org.jetbrains.compose.resources.DrawableResource

fun TastyMapIcon.toAndroidRes(): DrawableResource {
    return when (this) {
        TastyMapIcon.LOCATION -> Res.drawable.location
        TastyMapIcon.STAR -> Res.drawable.star
    }
}
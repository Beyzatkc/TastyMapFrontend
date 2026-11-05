package org.beem.tastymap.ui.tastyview

import org.beem.tastymap.ui.tastyview.icons.TastyMapIcon
import org.jetbrains.compose.resources.DrawableResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.location
import tastymap.composeapp.generated.resources.star

fun TastyMapIcon.toAndroidRes(): DrawableResource {
    return when (this) {
        TastyMapIcon.LOCATION -> Res.drawable.location
        TastyMapIcon.STAR -> Res.drawable.star
    }
}
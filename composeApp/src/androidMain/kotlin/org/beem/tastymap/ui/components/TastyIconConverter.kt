package org.beem.tastymap.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

fun TastyIcon.getMobileIcon(): ImageVector {
    return when (this) {
        TastyIcon.LOCATION -> Icons.Default.LocationOn
        TastyIcon.ADD -> Icons.Default.Add
        TastyIcon.SEARCH -> Icons.Default.Search
        TastyIcon.FILTER -> Icons.Default.FilterList
    }
}
package org.beem.tastymap.ui.tastyview

import androidx.compose.runtime.Composable

@Composable
expect fun TastyBottomSheet(
    sheetState: TastyBottomSheetState,
    displayMode: SheetDisplayMode = SheetDisplayMode.REPLACE,
    widthPercentage: Int,
    cornerRadius: Int,
    backgroundColor: String,
    onDismiss: () -> Unit,
    content: () -> TastyView
)
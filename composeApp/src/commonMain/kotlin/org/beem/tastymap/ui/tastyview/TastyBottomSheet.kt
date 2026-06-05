package org.beem.tastymap.ui.tastyview

import androidx.compose.runtime.Composable

@Composable
expect fun TastyBottomSheet(
    sheetState: TastyBottomSheetState,
    displayMode: SheetDisplayMode = SheetDisplayMode.REPLACE,
    widthPercentage: Int = 90,
    cornerRadius: Int = 24,
    backgroundColor: String = "#FFFFFF",
    onDismiss: () -> Unit,
    content: () -> TastyView
)
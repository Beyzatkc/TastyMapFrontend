package org.beem.tastymap.ui.tastyview

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun TastyBottomSheet(
    sheetState: TastyBottomSheetState,
    displayMode: SheetDisplayMode,
    widthPercentage: Int,
    cornerRadius: Int,
    backgroundColor: String,
    onDismiss: () -> Unit,
    content: () -> TastyView
) {
    val dragHandleColor = "#4B5563"
    val state = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope = rememberCoroutineScope()

    LaunchedEffect(sheetState) {
        sheetState.nativeCloseHandler = {
            scope.launch {
                state.hide()
                onDismiss()
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = state,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(
            topStart = cornerRadius.dp,
            topEnd = cornerRadius.dp
        ),
        containerColor = androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor(backgroundColor)),
        dragHandle = {
            androidx.compose.material3.BottomSheetDefaults.DragHandle(
                color = androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor(dragHandleColor))
            )
        }
    ) {
        content().render()
    }
}
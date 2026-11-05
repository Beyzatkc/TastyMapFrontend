package org.beem.tastymap.ui.search

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.beem.tastymap.search.state.SearchIntent
import org.beem.tastymap.search.state.SearchUiState

@Composable
actual fun TastySearchOverlay(
    uiState: SearchUiState,
    onIntent: (SearchIntent) -> Unit,
    modifier: Modifier
) {

}
package org.beem.tastymap.ui.search

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.beem.tastymap.search.state.SearchUiState
import org.beem.tastymap.search.model.SearchVenue
import org.beem.tastymap.search.state.SearchIntent

@Composable
expect fun TastySearchOverlay(
    uiState: SearchUiState,
    onIntent: (SearchIntent) -> Unit,
    modifier: Modifier = Modifier
)
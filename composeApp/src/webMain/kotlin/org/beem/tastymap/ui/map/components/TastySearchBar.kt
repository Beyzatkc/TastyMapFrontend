package org.beem.tastymap.ui.map.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
actual fun TastySearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier,
    placeholder: String,
    onSearchBarClicked: (() -> Unit)?
) {

}
package org.beem.tastymap.ui.map.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun TastySearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Restoran, kafe veya lezzet ara...",
    onSearchBarClicked: (() -> Unit)? = null
)
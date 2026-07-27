package org.beem.tastymap.ui.detailsheet

import androidx.compose.runtime.Composable
import org.beem.tastymap.data.model.Restaurant
import org.beem.tastymap.place.state.ReviewPagingState

@Composable
expect fun TastyDetailSheet(
    restaurant: Restaurant,
    pagingState: ReviewPagingState,
    onLoadMoreReviews: () -> Unit,
    onDismiss: () -> Unit
)
package org.beem.tastymap.ui.detailsheet

import androidx.compose.runtime.Composable
import org.beem.tastymap.core.paging.TastyPagingState
import org.beem.tastymap.data.model.Restaurant
import org.beem.tastymap.place.model.review.ReviewItem
import org.beem.tastymap.place.state.ReviewPagingState

@Composable
expect fun TastyDetailSheet(
    restaurant: Restaurant,
    pagingState: TastyPagingState<ReviewItem>,
    onRender: (newState: TastyPagingState<ReviewItem>) -> Unit,
    onLoadMoreReviews: () -> Unit,
    onDismiss: () -> Unit
)
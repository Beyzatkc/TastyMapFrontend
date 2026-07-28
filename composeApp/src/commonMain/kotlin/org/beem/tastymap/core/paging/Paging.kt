package org.beem.tastymap.core.paging

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue


interface PagingController {
    fun loadMore()
}


class PagingState {
    var controller: PagingController?  by mutableStateOf(null)

    fun loadMore() {
        controller?.loadMore()
    }

}
package org.beem.tastymap.ui.tastyview.paginglist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.beem.tastymap.ui.tastyview.TastyPlatformView
import org.beem.tastymap.ui.tastyview.toAndroidModifier

@Suppress("UNCHECKED_CAST")
internal actual fun <T> platformRender(pagingList: TastyPagingList<T>): TastyPlatformView {
    return TastyPlatformView {
        val lazyListState = rememberLazyListState()
        val controller = pagingList.controller

        LaunchedEffect(Unit) {
            if(controller.itemsList.isEmpty()){
                controller.loadNextPage()
            }
        }

        val shouldLoadNextPage by remember {
            derivedStateOf {
                val layoutInfo = lazyListState.layoutInfo
                val totalItemsCount = layoutInfo.totalItemsCount
                val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1

                totalItemsCount > 0 && lastVisibleItemIndex >= (totalItemsCount - 2)
            }
        }

        LaunchedEffect(shouldLoadNextPage) {
            if (shouldLoadNextPage) {
                controller.loadNextPage()
            }
        }

        Box(modifier = pagingList.modifier.toAndroidModifier().fillMaxSize()) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier.fillMaxSize()
            ) {
                // Header çizimi
                pagingList.headerTemplate?.let { header ->
                    item(key = "tasty_paging_header") {
                        header().render().content()
                    }
                }

                // Canlı canlı ortak katmandaki listeyi çiziyoruz
                items(controller.itemsList) { item ->
                    pagingList.itemTemplate(item).render().content()
                }

                // Shimmer/Yükleniyor çizimi (Durumu yine ortak katmandan okuyoruz)
                if (controller.isLoading && !controller.isEndOfTheList) {
                    item(key = "tasty_paging_loading") {
                        pagingList.loadingTemplate().render().content()
                    }
                }
            }
        }
    }
}
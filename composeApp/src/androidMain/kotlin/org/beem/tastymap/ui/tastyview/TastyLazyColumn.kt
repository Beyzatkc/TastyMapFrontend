package org.beem.tastymap.ui.tastyview


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

actual class TastyLazyColumn actual constructor(
    private val key: String,
    private val items: List<TastyView>,
    private val onLoadMore: () -> Unit
) : TastyView {

    override actual fun render(): TastyPlatformView {
        return TastyPlatformView {
            val listState = rememberLazyListState()

            val shouldLoadMore by remember {
                derivedStateOf {
                    val layoutInfo = listState.layoutInfo
                    val totalItemsCount = layoutInfo.totalItemsCount
                    val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0)

                    // Liste boş değilse ve son eleman ekrana geldiyse onLoadMore'u tetikle
                    totalItemsCount > 0 && lastVisibleItemIndex >= (totalItemsCount - 2)
                }
            }

            // Koşul sağlandığı an ortak katmandan gelen onLoadMore() fonksiyonunu ateşliyoruz
            LaunchedEffect(shouldLoadMore) {
                if (shouldLoadMore) {
                    onLoadMore()
                }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Android'in yerel LazyColumn hücre yapısı
                itemsIndexed(
                    items = items,
                    // Eğer ortak katmandan jenerik bir item-key yapısı kurduysan buraya bağlayabilirsin dayıcım,
                    // şimdilik indeks + listenin kendi key'i ile benzersiz kılıyoruz.
                    key = { index, _ -> "$key-$index" }
                ) { _, item ->
                    // 🚀 Her hücrenin kendi iç ağacını tetikleyip ekrana native çizdiriyoruz!
                    item.render().content()
                }
            }
        }
    }
}
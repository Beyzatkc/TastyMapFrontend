package org.beem.tastymap.ui.tastyview.paginglist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TastyPagingController<T>(
    val pageSize: Int = 10,
    private val onLoadNextPage: suspend (page: Int, pageSize: Int) -> List<T>
) {
    var currentPage by mutableStateOf(0)
    val itemsList = mutableStateListOf<T>()


    var isLoading by mutableStateOf(false)
    var isEndOfTheList by mutableStateOf(false)


    private var isRequestInProgress = false

    private val scope = CoroutineScope(Dispatchers.Main)

    fun loadNextPage() {
        if (isLoading || isEndOfTheList || isRequestInProgress) return

        isRequestInProgress = true
        isLoading = true

        scope.launch {
            try {
                println("--- TastyPagingController --- Sayfa İsteniyor: $currentPage")
                val newPageData = onLoadNextPage(currentPage, pageSize)

                if (newPageData.isEmpty() || newPageData.size < pageSize) {
                    isEndOfTheList = true
                }
                if(newPageData.isNotEmpty()){
                    itemsList.addAll(newPageData)
                    currentPage++
                }
            } catch (e: Exception) {
                println("--- TastyPaging Hata Kalkanı ---")
                e.printStackTrace()
            } finally {
                isLoading = false
                isRequestInProgress = false
            }
        }
    }

    fun reset() {
        currentPage = 0
        itemsList.clear()
        isEndOfTheList = false
        isLoading = false
        isRequestInProgress = false
    }

}
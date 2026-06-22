package org.beem.tastymap.ui.tastyview.paginglist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class TastyPagingController<T>(
    val pageSize: Int = 10,
    private val onLoadNextPage: suspend (page: Int, pageSize: Int) -> List<T>
) {
    var currentPage by mutableStateOf(1)
    val itemsList = mutableStateListOf<T>()
    var isLoading by mutableStateOf(false)
    var isEndOfTheList by mutableStateOf(false)


    suspend fun loadNextPage() {
        if (isLoading || isEndOfTheList) return

        isLoading = true
        try {
            println("--- TastyPagingController --- Sayfa İsteniyor: $currentPage")
            val newPageData = onLoadNextPage(currentPage, pageSize)

            if (newPageData.isEmpty() || newPageData.size < pageSize) {
                isEndOfTheList = true
            }

            itemsList.addAll(newPageData)
            currentPage++
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

}
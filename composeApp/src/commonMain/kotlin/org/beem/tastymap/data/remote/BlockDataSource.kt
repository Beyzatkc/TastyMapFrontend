package org.beem.tastymap.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import org.beem.tastymap.data.model.PageResponse
import org.beem.tastymap.data.model.block.BlockResponse

class BlockDataSource(private val client: HttpClient) {

    suspend fun blockUser(userId: Long){
        return client.post("api/block/$userId").body()
    }

    suspend fun unBlockUser(userId: Long){
        return client.delete("api/block/unBlock/$userId").body()
    }

    suspend fun getBlockedUsers(page: Int = 0, size: Int = 10):PageResponse<BlockResponse>{
        return client.get("api/block/getBlocks") {
            parameter("page", page)
            parameter("size", size)
        }.body()
    }

}
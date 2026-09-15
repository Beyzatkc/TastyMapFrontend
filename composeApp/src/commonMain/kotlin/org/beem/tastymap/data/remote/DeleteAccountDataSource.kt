package org.beem.tastymap.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.setBody
import org.beem.tastymap.data.model.deleteaccount.DeleteAccountRequest

class DeleteAccountDataSource(private val client: HttpClient) {

    suspend fun deleteAccount(request: DeleteAccountRequest){
        return client.delete("api/account/me/delete"){
            setBody(request)
        }.body()
    }
}
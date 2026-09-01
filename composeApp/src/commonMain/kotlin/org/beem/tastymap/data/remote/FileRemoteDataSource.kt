package org.beem.tastymap.data.remote

import io.github.vinceglb.filekit.core.PlatformFile
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import org.beem.tastymap.data.model.file.FileUploadResponse

class FileRemoteDataSource(private val client: HttpClient) {

    suspend fun uploadFile(file: PlatformFile): FileUploadResponse {
        val bytes = file.readBytes()
        val fileName = file.name ?: "profile_image.jpg"

        return client.submitFormWithBinaryData(
            url = "api/v1/files/upload",
            formData = formData {
                append("file", bytes, Headers.build {
                    append(HttpHeaders.ContentType, "image/jpeg")
                    append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                })
            }
        ).body()
    }
}
package org.beem.tastymap.data.remote

import io.github.vinceglb.filekit.core.PlatformFile
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import org.beem.tastymap.core.network.AuthHttpClientManager
import org.beem.tastymap.core.util.ImageCompressor
import org.beem.tastymap.data.model.file.FileUploadResponse
import org.beem.tastymap.data.model.file.MultipleFileUploadResponse

class FileRemoteDataSource(private val authHttpClientManager: AuthHttpClientManager) {
    private val client: HttpClient
        get() = authHttpClientManager.getClient()

    suspend fun uploadFile(file: PlatformFile, type: String = "profiles"): FileUploadResponse {
        val rawBytes = file.readBytes()

        val compressedBytes = ImageCompressor.compress(
            bytes = rawBytes,
            maxWidth = 1024,
            maxHeight = 1024,
            quality = 75
        )

        val fileName = file.name

        return client.submitFormWithBinaryData(
            url = "api/v1/files/upload/$type",
            formData = formData {
                append("file", compressedBytes, Headers.build {
                    append(HttpHeaders.ContentType, "image/jpeg")
                    append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                })
            }
        ).body()
    }
    suspend fun uploadMultipleFiles(
        imagesBytes: List<ByteArray>,
        type: String = "posts"
    ): MultipleFileUploadResponse {
        return client.post("api/v1/files/upload-multiple/$type") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        imagesBytes.forEachIndexed { index, bytes ->
                            append(
                                key = "files",
                                value = bytes,
                                headers = Headers.build {
                                    append(HttpHeaders.ContentType, "image/jpeg")
                                    append(
                                        HttpHeaders.ContentDisposition,
                                        "filename=\"photo_$index.jpg\""
                                    )
                                }
                            )
                        }
                    }
                )
            )
        }.body()
    }
}
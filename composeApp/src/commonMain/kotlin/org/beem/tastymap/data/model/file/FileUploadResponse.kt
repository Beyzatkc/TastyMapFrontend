package org.beem.tastymap.data.model.file

import kotlinx.serialization.Serializable

@Serializable
data class FileUploadResponse(
    val imageUrl: String
)
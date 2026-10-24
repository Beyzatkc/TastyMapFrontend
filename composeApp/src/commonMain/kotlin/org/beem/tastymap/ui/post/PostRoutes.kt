package org.beem.tastymap.ui.post

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("post-graph")
data object PostGraph

@Serializable
@SerialName("editpost")
data class EditPostRoute(val postId: Long)

@Serializable
@SerialName("post")
data class PostDetailRoute(val postId: Long)

@Serializable
@SerialName("createpost")
data object CreatePostRoute
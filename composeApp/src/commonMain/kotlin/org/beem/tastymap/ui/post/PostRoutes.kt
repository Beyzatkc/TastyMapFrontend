package org.beem.tastymap.ui.post

import kotlinx.serialization.Serializable
@Serializable data object PostGraph
@Serializable
data class EditPostRoute(val postId: Long)

@Serializable
data class PostDetailRoute(val postId: Long)

@Serializable
data object CreatePostRoute
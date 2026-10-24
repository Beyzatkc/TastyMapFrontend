package org.beem.tastymap.ui.bottomnav

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("main")
data object MainRoute

@Serializable
@SerialName("map")
data object MapTabRoute

@Serializable
@SerialName("search")
data object SearchTabRoute

@Serializable
@SerialName("ai")
data object AITabRoute

@Serializable
@SerialName("upload")
data object UploadTabRoute

@Serializable
@SerialName("myprofile")
data object ProfileTabRoute
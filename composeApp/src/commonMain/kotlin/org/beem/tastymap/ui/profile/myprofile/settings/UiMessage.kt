package org.beem.tastymap.ui.profile.myprofile.settings

import org.jetbrains.compose.resources.StringResource

sealed interface UiMessage {
    data class Dynamic(val message: String) : UiMessage
    data class Resource(val res: StringResource, val args: List<Any> = emptyList()) : UiMessage
}
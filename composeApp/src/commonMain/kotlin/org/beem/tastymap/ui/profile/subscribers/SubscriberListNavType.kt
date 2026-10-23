package org.beem.tastymap.ui.profile.subscribers

import androidx.navigation.NavType
import androidx.savedstate.SavedState
import androidx.savedstate.read
import androidx.savedstate.write
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val SubscriberListNavType = object : NavType<SubscriberListType>(isNullableAllowed = false) {
    override fun get(bundle: SavedState, key: String): SubscriberListType? {
        val jsonString = bundle.read { getStringOrNull(key) }
        return jsonString?.let { Json.decodeFromString<SubscriberListType>(it) }
    }

    override fun parseValue(value: String): SubscriberListType {
        return Json.decodeFromString(value)
    }

    override fun put(bundle: SavedState, key: String, value: SubscriberListType) {
        bundle.write { putString(key, Json.encodeToString(value)) }
    }

    override fun serializeAsValue(value: SubscriberListType): String {
        return Json.encodeToString(value)
    }
}
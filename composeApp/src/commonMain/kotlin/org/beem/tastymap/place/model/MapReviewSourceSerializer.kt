package org.beem.tastymap.place.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object MapReviewSourceSerializer : KSerializer<MapReviewSource> {
    override val descriptor: SerialDescriptor
        = PrimitiveSerialDescriptor("MapReviewSource", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: MapReviewSource) {
        val stringValue = when(value){
            MapReviewSource.GOOGLE -> "GOOGLE"
            MapReviewSource.INTERNAL -> "INTERNAL"
            else -> "UNKNOWN"
        }
        encoder.encodeString(stringValue)
    }

    override fun deserialize(decoder: Decoder): MapReviewSource {
        return when (val value = decoder.decodeString()) {
            "GOOGLE" -> MapReviewSource.GOOGLE
            "INTERNAL" -> MapReviewSource.INTERNAL
            else -> MapReviewSource.UNKNOWN
        }
    }
}
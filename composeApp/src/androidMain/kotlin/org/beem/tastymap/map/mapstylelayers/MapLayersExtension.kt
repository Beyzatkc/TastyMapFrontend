package org.beem.tastymap.map.mapstylelayers

import android.content.Context
import androidx.compose.ui.graphics.toColorLong
import org.beem.tastymap.R
import org.beem.tastymap.map.createMarkerBitmap
import org.beem.tastymap.ui.theme.AppColors
import org.beem.tastymap.ui.utils.toAndroidColor
import org.maplibre.android.maps.Style
import org.maplibre.android.style.expressions.Expression
import org.maplibre.android.style.layers.Property
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.layers.SymbolLayer
import org.maplibre.android.style.sources.GeoJsonSource

fun Style.setupUserLocationLayer(context: Context, iconId: String, iconRes: Int) {
    createMarkerBitmap(context, iconRes, 30)?.let { addImage(iconId, it) }

    val source = GeoJsonSource("user-location-source")
    addSource(source)

    val layer = SymbolLayer("user-location-layer", "user-location-source").withProperties(
        PropertyFactory.iconImage(iconId),
        PropertyFactory.iconRotate(Expression.get("bearing")),
        PropertyFactory.iconRotationAlignment(Property.ICON_ROTATION_ALIGNMENT_MAP),
        PropertyFactory.iconAllowOverlap(true),
        PropertyFactory.iconIgnorePlacement(true)
    )
    addLayer(layer)
}

fun Style.setupRestaurantLayer(context: Context) {
    val iconSizeDp = 42
    val selectedPinSizeDp = 54

    createMarkerBitmap(context, R.drawable.ic_restaurant_v2, iconSizeDp)?.let { addImage("tm_restaurant", it) }
    createMarkerBitmap(context, R.drawable.ic_marker_bakery, iconSizeDp)?.let { addImage("tm_bakery", it) }
    createMarkerBitmap(context, R.drawable.ic_marker_cafe, iconSizeDp)?.let { addImage("tm_cafe", it) }
    createMarkerBitmap(context, R.drawable.ic_marker_default_v2, iconSizeDp)?.let { addImage("tm_default", it) }

    createMarkerBitmap(context, R.drawable.ic_selected_search_pin, selectedPinSizeDp)?.let {
        addImage("tm_selected_search_pin", it)
    }

    val source = GeoJsonSource("restaurant-source")
    addSource(source)

    val layer = SymbolLayer("restaurant-layer", "restaurant-source").withProperties(
        PropertyFactory.iconImage(
            Expression.match(
                Expression.get("category"),
                Expression.literal("bakery"), Expression.literal("tm_bakery"),
                Expression.literal("cafe"), Expression.literal("tm_cafe"),
                Expression.literal("restaurant"), Expression.literal("tm_restaurant"),
                Expression.literal("tm_default")
            )
        ),

        PropertyFactory.iconAnchor(Property.ICON_ANCHOR_BOTTOM),
        PropertyFactory.iconSize(0.92f),

        PropertyFactory.iconAllowOverlap(true),
        PropertyFactory.iconIgnorePlacement(true),

        PropertyFactory.textField(Expression.get("name")),

        PropertyFactory.textAnchor(Property.TEXT_ANCHOR_TOP),

        PropertyFactory.textOffset(arrayOf(0f, 0.65f)),
        PropertyFactory.textSize(12f),
        PropertyFactory.textColor(
            AppColors.NavyBlue.toAndroidColor()
        ),
        PropertyFactory.textHaloColor(AppColors.BackBackgroundBlue.toAndroidColor()),

        PropertyFactory.textHaloWidth(1.75f),

        PropertyFactory.textHaloBlur(0.5f),

        PropertyFactory.textAllowOverlap(false),

        PropertyFactory.textOptional(true),

        PropertyFactory.iconOpacity(
            Expression.step(
                Expression.zoom(),
                Expression.literal(0f),
                Expression.stop(Expression.get("min_zoom"), Expression.literal(1f))
            )
        ),

        PropertyFactory.textOpacity(
            Expression.step(
                Expression.zoom(),
                Expression.literal(0f),
                Expression.stop(Expression.get("min_zoom"), Expression.literal(1f))
            )
        )
    )
    addLayer(layer)
}
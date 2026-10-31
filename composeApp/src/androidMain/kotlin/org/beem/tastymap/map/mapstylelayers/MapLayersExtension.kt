package org.beem.tastymap.map.mapstylelayers

import android.content.Context
import androidx.compose.ui.graphics.toColorLong
import org.beem.tastymap.R
import org.beem.tastymap.map.MapLayerConstants
import org.beem.tastymap.map.createMarkerBitmap
import org.beem.tastymap.ui.theme.AppColors
import org.beem.tastymap.ui.utils.toAndroidColor
import org.maplibre.android.maps.Style
import org.maplibre.android.style.expressions.Expression
import org.maplibre.android.style.layers.Property
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.layers.SymbolLayer
import org.maplibre.android.style.sources.GeoJsonSource


fun Style.setupUserLocationLayer(context: Context) {
    val userNavIconId = MapLayerConstants.USER_NAVIGATION_ICON_ID
    // 64dp olarak bitmap oluşturup stilde tanımlıyoruz
    createMarkerBitmap(context, R.drawable.ic_user_location_marker, 64)?.let {
        addImage(userNavIconId, it)
    }

    val userSourceId = MapLayerConstants.USER_LOCATION_SOURCE_ID
    val userLayerId = MapLayerConstants.USER_LOCATION_LAYER_ID

    val source = GeoJsonSource(userSourceId)
    addSource(source)

    val layer = SymbolLayer(userLayerId, userSourceId).withProperties(
        PropertyFactory.iconImage(userNavIconId),

        // Bearing verisiyle harita yönüne göre fırıldak gibi dönmesini sağlar:
        PropertyFactory.iconRotate(Expression.get("bearing")),
        PropertyFactory.iconRotationAlignment(Property.ICON_ROTATION_ALIGNMENT_MAP),

        // Daire tam (128, 128) merkezinde olduğu için tam orta noktadan dönecek:
        PropertyFactory.iconAnchor(Property.ICON_ANCHOR_CENTER),

        PropertyFactory.iconAllowOverlap(true),
        PropertyFactory.iconIgnorePlacement(true)
    )
    if (getLayer(MapLayerConstants.RESTAURANT_LAYER_ID) != null) {
        addLayerAbove(layer, MapLayerConstants.RESTAURANT_LAYER_ID)
    } else {
        addLayer(layer)
    }
}


fun Style.setupRestaurantLayer(context: Context) {
    val iconSizeDp = 42
    val selectedPinSizeDp = 54

    val icRestaurant = MapLayerConstants.RESTAURANT_ICON_ID
    val icBakery = MapLayerConstants.BAKERY_ICON_ID
    val icCafe = MapLayerConstants.CAFE_ICON_ID
    val icDefault = MapLayerConstants.DEFAULT_ICON_ID
    val icSelected = MapLayerConstants.SELECTED_PIN_ICON_ID

    createMarkerBitmap(context, R.drawable.ic_restaurant_v2, iconSizeDp)?.let { addImage(icRestaurant, it) }
    createMarkerBitmap(context, R.drawable.ic_marker_bakery, iconSizeDp)?.let { addImage(icBakery, it) }
    createMarkerBitmap(context, R.drawable.ic_marker_cafe, iconSizeDp)?.let { addImage(icCafe, it) }
    createMarkerBitmap(context, R.drawable.ic_marker_default_v2, iconSizeDp)?.let { addImage(icDefault, it) }

    createMarkerBitmap(context, R.drawable.ic_selected_search_pin, selectedPinSizeDp)?.let {
        addImage(icSelected, it)
    }

    val restaurantSourceId = MapLayerConstants.RESTAURANT_SOURCE_ID
    val restaurantLayerId = MapLayerConstants.RESTAURANT_LAYER_ID

    val source = GeoJsonSource(restaurantSourceId)
    addSource(source)

    val layer = SymbolLayer(restaurantLayerId, restaurantSourceId).withProperties(
        PropertyFactory.iconImage(
            Expression.match(
                Expression.get("category"),
                Expression.literal("bakery"), Expression.literal(MapLayerConstants.BAKERY_ICON_ID),
                Expression.literal("cafe"), Expression.literal(MapLayerConstants.CAFE_ICON_ID),
                Expression.literal("restaurant"), Expression.literal(MapLayerConstants.RESTAURANT_ICON_ID),
                Expression.literal(MapLayerConstants.DEFAULT_ICON_ID)
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
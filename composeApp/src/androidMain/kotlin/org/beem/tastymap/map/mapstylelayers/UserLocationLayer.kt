import android.content.Context
import org.beem.tastymap.map.createMarkerBitmap
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

fun Style.setupRestaurantLayer(context: Context, iconId: String, iconRes: Int) {
    createMarkerBitmap(context, iconRes, 24)?.let { addImage(iconId, it) }

    val source = GeoJsonSource("restaurant-source")
    addSource(source)

    val layer = SymbolLayer("restaurant-layer", "restaurant-source").withProperties(
        PropertyFactory.iconImage(iconId),
        PropertyFactory.iconAllowOverlap(true),
        PropertyFactory.iconIgnorePlacement(true),
        PropertyFactory.textField(Expression.get("name")),
        PropertyFactory.textOffset(arrayOf(0f, 1.5f)),
        PropertyFactory.textSize(12f),
        PropertyFactory.textColor(android.graphics.Color.BLACK)
    )
    addLayer(layer)
}
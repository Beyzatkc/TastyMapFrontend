package org.beem.tastymap.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import org.beem.tastymap.data.model.LocationData
import org.w3c.dom.HTMLDivElement


@JsFun("""
(function(element, url, lat, lng, isAvailable) {
    if (typeof maplibregl === 'undefined' || !element) return;

    if (!element._mapInstance) {
        element._mapInstance = new maplibregl.Map({
            container: element,
            style: url,
            center: [lng, lat],
            zoom: 15,
            trackResize: true
        });

        element._mapInstance.on('load', function() {
            // Kaynağı ekle
            element._mapInstance.addSource('user-location', {
                type: 'geojson',
                data: {
                    type: 'Feature',
                    geometry: { type: 'Point', coordinates: [lng, lat] }
                }
            });

            // Beyaz Dış Çerçeve
            element._mapInstance.addLayer({
                id: 'user-dot-outer',
                type: 'circle',
                source: 'user-location',
                paint: {
                    'circle-radius': 10,
                    'circle-color': '#ffffff',
                    'circle-stroke-width': 2,
                    'circle-stroke-color': '#cccccc'
                }
            });

            // Mavi İç Nokta
            element._mapInstance.addLayer({
                id: 'user-dot-inner',
                type: 'circle',
                source: 'user-location',
                paint: {
                    'circle-radius': 6,
                    'circle-color': '#007AFF'
                }
            });
            
            // Haritayı bir kere zorla boyutlandır
            setTimeout(() => element._mapInstance.resize(), 300);
        });

    } else {
        const map = element._mapInstance;
        const src = map.getSource('user-location');
        
        if (src && isAvailable) {
            // Veriyi güncelle (Marker kayar)
            src.setData({
                type: 'Feature',
                geometry: { type: 'Point', coordinates: [lng, lat] }
            });

            // Haritayı pürüzsüzce kullanıcının üzerine sür (Sıkıntı 1 Çözümü)
            map.easeTo({
                center: [lng, lat],
                duration: 600,
                essential: true
            });
        }
    }
    
    // Her güncellemede ufak bir resize (Sıkıntı 2 Çözümü)
    element._mapInstance.resize();
})
""")
private external fun initMapWithUser(
    element: org.w3c.dom.HTMLElement,
    url: String,
    lat: Double,
    lng: Double,
    isAvailable: Boolean
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun TastyMapComponent(
    modifier: Modifier,
    mapUrl: String,
    userLocation: LocationData,
    state: TastyMapState
) {
    WebElementView(
        modifier = modifier,
        factory = {
            val container = document.createElement("div") as HTMLDivElement
            // ID'ye artık bağımlı değiliz ama yine de kalsın
            container.id = "map-container"
            container.style.width = "100%"
            container.style.height = "100%"
            container.style.display = "block"
            container
        },
        update = { container ->
            initMapWithUser(
                container,
                mapUrl,
                userLocation.latitude,
                userLocation.longitude,
                userLocation.isAvailable
            )
        }
    )
}
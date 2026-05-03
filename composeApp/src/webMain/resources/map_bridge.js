window.initializeMapLibreJS = function(containerId) {
    console.log("JS: Harita baslatiliyor...");

    const map = new maplibregl.Map({
        container: containerId,
        style: 'https://api.maptiler.com/maps/019dbfbf-86a2-7d38-869e-bd6ebbcee298/style.json?key=DNr5GYdtJfA7ecaMmrh1',
        center: [32.49, 37.87],
        zoom: 12,
        trackResize: true
    });

    map.on('load', () => {
        console.log("MAP READY");
        // Mavi arka planı kaldır
        document.getElementById(containerId).style.background = "transparent";
        // Boyutları zorla güncelle
        setTimeout(() => { map.resize(); }, 100);
    });

    window.map = map;
};

window.listenMarkerClick = function(layerId, callback) {
    // Harita yüklenmeden click dinleyicisi eklenemez, o yüzden 'load' bekliyoruz
    window.map.on('load', () => {
        window.map.on('click', layerId, (e) => {
            const feature = e.features[0];
            const { id, name } = feature.properties;
            const [lng, lat] = feature.geometry.coordinates;

            // Kotlin callback'ini (id, name, lat, lng) olarak çağırır
            callback(id.toString(), name, lat, lng);
        });
    });
};

window.flyToWeb = function(lat, lng, zoom) {
    if(window.map) {
        window.map.flyTo({ center: [lng, lat], zoom: zoom, essential: true });
    }
};

window.updateWebSource = function(sourceId, geoJson) {
    if(window.map && window.map.getSource(sourceId)) {
        window.map.getSource(sourceId).setData(JSON.parse(geoJson));
    }
};
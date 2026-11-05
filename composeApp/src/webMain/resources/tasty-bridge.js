(function () {
    window.TastyMapBridge = {
        map: null,
        userLocationSourceId: "user-location-source",
        isMapLibreLoaded: false,

        animator: {
            lastLat: 0,
            lastLng: 0,
            lastBearing: 0,
            requestID: null
        },

        // 1. SDK Tembel Yükleme (Lazy Load)
        loadMapLibreSdk: function () {
            return new Promise((resolve, reject) => {
                // Eğer zaten yüklendiyse bekleme, hemen geç
                if (window.maplibregl || this.isMapLibreLoaded) {
                    this.isMapLibreLoaded = true;
                    resolve(null);
                    return;
                }

                console.log("Atlas: Harita ekranı tetiklendi. SDK dinamik olarak yükleniyor...");

                // 1. Önce CSS Enjekte Ediliyor
                const link = document.createElement('link');
                link.rel = 'stylesheet';
                link.href = 'https://unpkg.com/maplibre-gl@3.6.2/dist/maplibre-gl.css';
                document.head.appendChild(link);

                // 2. Sonra JS Enjekte Ediliyor
                const script = document.createElement('script');
                script.src = 'https://unpkg.com/maplibre-gl@3.6.2/dist/maplibre-gl.js';
                script.type = 'text/javascript';
                
                // Tarayıcının script'i tamamen okuyup execute ettiğinden emin oluyoruz
                script.onload = () => {
                    console.log("Atlas: MapLibre script dosyası tarayıcıya başarıyla parse edildi.");
                    this.isMapLibreLoaded = true;
                    resolve(null); // Kotlin Wasm/JS tarafına "hazırız" sinyali gönderiliyor
                };

                script.onerror = (err) => {
                    console.error("Atlas Hatası: Tarayıcı script yüklemesini reddetti:", err);
                    reject(new Error("MapLibre GL JS yüklenemedi. Ağ veya CSP engeli olabilir."));
                };

                document.head.appendChild(script);
            });
        },

        // Görsel Yükleme Yardımcısı
        loadImagePromise: function (id, path) {
            return new Promise((resolve) => {
                if (!this.map) return resolve();
                if (this.map.hasImage(id)) return resolve();

                const img = new Image();
                img.src = path;
                img.onload = () => {
                    if (this.map && !this.map.hasImage(id)) {
                        this.map.addImage(id, img);
                    }
                    resolve();
                };
                img.onerror = (e) => {
                    console.error(`Görsel decode edilemedi veya bulunamadı: ${path}`, e);
                    resolve();
                };
            });
        },

        setupRestaurantLayers: async function () {
            const icons = {
                'tm_restaurant': 'ic_restaurant.svg',
                'tm_bakery': 'ic_bakery.svg',
                'tm_cafe': 'ic_cafe.svg',
                'tm_default': 'ic_default.svg'
            };

            await Promise.all(Object.entries(icons).map(([id, path]) => this.loadImagePromise(id, path)));

            if (!this.map.getSource('restaurant-source')) {
                this.map.addSource('restaurant-source', {
                    type: 'geojson',
                    data: { "type": "FeatureCollection", "features": [] }
                });
            }

            if (!this.map.getLayer('restaurant-layer')) {
                this.map.addLayer({
                    id: 'restaurant-layer',
                    type: 'symbol',
                    source: 'restaurant-source',
                    layout: {
                        'icon-image': ['get', 'icon_to_use'],
                        'icon-size': ['get', 'icon_scale'],
                        'icon-allow-overlap': true,
                        'text-field': ['get', 'name'],
                        'text-offset': [0, 1.5],
                        'text-size': 11
                    },
                    paint: {
                        'text-color': '#000000',
                        'text-halo-color': '#FFFFFF',
                        'text-halo-width': 1,
                        'icon-opacity': ['step', ['zoom'], 0, 13, 1],
                        'text-opacity': ['step', ['zoom'], 0, 13, 1]
                    }
                });
            }
        },

        setupUserLocationLayer: async function () {
            await this.loadImagePromise('user-arrow-icon', 'navigation.png');

            if (!this.map.getSource(this.userLocationSourceId)) {
                this.map.addSource(this.userLocationSourceId, {
                    type: 'geojson',
                    data: {
                        "type": "Feature",
                        "geometry": { "type": "Point", "coordinates": [0, 0] },
                        "properties": { "bearing": 0 }
                    }
                });
            }

            if (!this.map.getLayer('user-location-layer')) {
                this.map.addLayer({
                    id: 'user-location-layer',
                    type: 'symbol',
                    source: this.userLocationSourceId,
                    layout: {
                        'icon-image': 'user-arrow-icon',
                        'icon-size': 0.1,
                        'icon-rotate': ['get', 'bearing'],
                        'icon-rotation-alignment': 'map',
                        'icon-allow-overlap': true,
                        'icon-ignore-placement': true
                    }
                });
            }
        },

        // 2. Güvenli ve Asenkron Map Başlatıcı
        initializeMap: async function (containerId, mapUrl) {
            if (this.map) return;

            const el = document.getElementById(containerId);
            if (!el) {
                console.error("DOM bulunamadı:", containerId);
                return;
            }

            // Önce SDK'nın yüklendiğinden emin ol
            try {
                await this.loadMapLibreSdk();
            } catch (err) {
                console.error("Harita SDK yükleme hatası:", err);
                return;
            }

            this.map = new maplibregl.Map({
                container: containerId,
                style: mapUrl,
                center: [32.49, 37.87],
                zoom: 12
            });

            // Stil yüklendiğinde katmanları oluştur
            this.map.on('style.load', async () => {
                await this.setupRestaurantLayers();
                await this.setupUserLocationLayer();
                console.log("TastyMap katmanları ve kaynakları başarıyla yüklendi.");
            });
        },

        flyTo: function (lat, lng, zoom) {
            if (this.map) {
                this.map.flyTo({
                    center: [lng, lat],
                    zoom: zoom,
                    essential: true
                });
            }
        },

        updateGeoJson: function (sourceId, data) {
            if (!this.map) return;
            const source = this.map.getSource(sourceId);
            if (source) {
                try {
                    const parsedData = typeof data === 'string' ? JSON.parse(data) : data;
                    source.setData(parsedData);
                } catch (e) {
                    console.error(`GeoJSON parse hatası (${sourceId}):`, e);
                }
            }
        },

        onMarkerClick: function (layerId, callback) {
            if (!this.map) return;
            this.map.on('click', layerId, (e) => {
                if (e.features && e.features.length > 0) {
                    const props = e.features[0].properties;
                    callback(JSON.stringify(props));
                }
            });

            this.map.on('mouseenter', layerId, () => { this.map.getCanvas().style.cursor = 'pointer'; });
            this.map.on('mouseleave', layerId, () => { this.map.getCanvas().style.cursor = ''; });
        },

        updateUserMarker: function (lat, lng, bearing) {
            const self = this;
            const startLat = self.animator.lastLat === 0 ? lat : self.animator.lastLat;
            const startLng = self.animator.lastLng === 0 ? lng : self.animator.lastLng;
            const startBearing = self.animator.lastBearing;

            const startTime = performance.now();
            const duration = 1000;

            if (self.animator.requestID) cancelAnimationFrame(self.animator.requestID);

            function animate(currentTime) {
                const elapsed = currentTime - startTime;
                const fraction = Math.min(elapsed / duration, 1);

                const currentLat = startLat + (lat - startLat) * fraction;
                const currentLng = startLng + (lng - startLng) * fraction;
                const currentBearing = startBearing + (bearing - startBearing) * fraction;

                const feature = {
                    "type": "Feature",
                    "geometry": { "type": "Point", "coordinates": [currentLng, currentLat] },
                    "properties": { "bearing": currentBearing }
                };

                const source = self.map ? self.map.getSource(self.userLocationSourceId) : null;
                if (source) source.setData(feature);

                self.animator.lastLat = currentLat;
                self.animator.lastLng = currentLng;
                self.animator.lastBearing = currentBearing;

                if (fraction < 1) {
                    self.animator.requestID = requestAnimationFrame(animate);
                }
            }
            self.animator.requestID = requestAnimationFrame(animate);
        },


        setCanvasCursor: function (cursorType) {
            if (this.map) {
                this.map.getCanvas().style.cursor = cursorType;
            }
        },

        addLayerClickListener: function (layerId, callback) {
            if (this.map) {
                this.map.on('click', layerId, (e) => {
                    if (e.features && e.features.length > 0) {
                        const props = e.features[0].properties;
                        callback(JSON.stringify(props));
                    }
                });
            }
        },

        addLayerHoverListener: function (layerId) {
            if (this.map) {
                const map = this.map;
                map.on('mouseenter', layerId, () => {
                    map.getCanvas().style.cursor = 'pointer';
                });
                map.on('mouseleave', layerId, () => {
                    map.getCanvas().style.cursor = '';
                });
            }
        }
    };
})();
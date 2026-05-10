(function() {
    window.TastyMapBridge = {
        map: null,

        userLocationSourceId: "user-location-source",


        animator: {
            lastLat: 0,
            lastLng: 0,
            lastBearing: 0,
            requestID: null
        },

        setupRestaurantLayers: async function() {
                const self = this;

                // 1. İkon listesini tanımla
                const icons = {
                    'tm_restaurant': 'ic_restaurant.svg',
                    'tm_bakery': 'ic_bakery.svg',
                    'tm_cafe': 'ic_cafe.svg',
                    'tm_default': 'ic_default.svg'
                };

                const loadIcon = (id, path) => {
                    return new Promise((resolve) => {
                        const img = new Image();
                        img.src = path;
                        img.onload = () => {
                            if (!this.map.hasImage(id)) {
                                this.map.addImage(id, img);
                            }
                            resolve();
                        };
                        img.onerror = (e) => {
                            console.error(`Görsel decode edilemedi veya bulunamadı: ${path}`, e);
                            resolve();
                        };
                    });
                };

                await Promise.all(Object.entries(icons).map(([id, path]) => loadIcon(id, path)));
                console.log("Tüm restoran ikonları hazır.");

                
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

        initializeMap: function(containerId, mapUrl) {
            const self = this;
            if (this.map) return;

            const el = document.getElementById(containerId);
            if (!el) {
                console.error("DOM bulunamadı:", containerId);
                return;
            }

            this.map = new maplibregl.Map({
                container: containerId,
                style: mapUrl,
                center: [32.49, 37.87],
                zoom: 12
            });

            const userImage = new Image();
                userImage.src = 'navigation.png';

                userImage.onload = () => {
                    console.log("Navigasyon ikonu yüklendi, haritaya eklenmeye hazır.");

                    // Harita stili her yüklendiğinde (veya değiştiğinde) ikonları ve katmanları tazele
                    this.map.on('style.load', () => {
                        self.setupRestaurantLayers();
                        console.log("Stil yüklendi, katmanlar kuruluyor...");

                        // Resmi harita hafızasına ekle
                        if (!this.map.hasImage('user-arrow-icon')) {
                            this.map.addImage('user-arrow-icon', userImage);
                        }

                        // Kaynağı ekle
                        if (!this.map.getSource(this.userLocationSourceId)) {
                            this.map.addSource(this.userLocationSourceId, {
                                type: 'geojson',
                                data: { "type": "Feature", "geometry": { "type": "Point", "coordinates": [0, 0] }, "properties": { "bearing": 0 } }
                            });
                        }

                        // Katmanı ekle (İşte o if bloğu)
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
                            console.log("Kullanıcı ikon katmanı BAŞARIYLA eklendi!");
                        }
                    });
                };

                userImage.onerror = () => {
                    console.error("navigation.png yüklenirken hata oluştu! Dosya yolu doğru mu? (index.html ile yan yana olmalı)");
                };
        },

        flyTo: function(lat, lng, zoom) {
            if (this.map) {
                this.map.flyTo({
                    center: [lng, lat],
                    zoom: zoom,
                    essential: true
                });
            }
        },

        updateGeoJson: function(sourceId, data) {
            if (!this.map) return;
            const source = this.map.getSource(sourceId);
            if (source) {
                source.setData(JSON.parse(data));
            }
        },

        onMarkerClick: function(layerId, callback) {
            this.map.on('click', layerId, (e) => {
                const props = e.features[0].properties;
                callback(JSON.stringify(props));
            });

            this.map.on('mouseenter', layerId, () => { this.map.getCanvas().style.cursor = 'pointer'; });
            this.map.on('mouseleave', layerId, () => { this.map.getCanvas().style.cursor = ''; });
        },

        updateUserMarker: function(lat, lng, bearing) {
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

                const source = self.map.getSource(self.userLocationSourceId);
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
    };
})();

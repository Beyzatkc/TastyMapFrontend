export default class MapProvider {
    constructor(container, url, lat, lng) {
        this.map = new maplibregl.Map({
            container: container,
            style: url,
            center: [lng, lat],
            zoom: 15,
            trackResize: true
        });

        this.map.on('load', () => {
            console.log("MapProvider: Harita hazır.");
            this.map.resize();
        });
    }

    updateUserLocation(lat, lng) {
        if (!this.map) return;
        // Pürüzsüz kamera hareketi
        this.map.easeTo({
            center: [lng, lat],
            duration: 600
        });
    }

    resize() {
        if (this.map) this.map.resize();
    }

    destroy() {
        if (this.map) {
            this.map.remove();
            this.map = null;
        }
    }
}
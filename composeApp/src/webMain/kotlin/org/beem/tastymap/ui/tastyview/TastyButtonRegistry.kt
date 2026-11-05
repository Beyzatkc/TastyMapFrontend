package org.beem.tastymap.ui.tastyview

// 1. ADIM: JavaScript ile Wasm arasındaki tüm köprü yönetimini en tepede kuruyoruz.
// Burada hiçbir Kotlin değişkeni doğrudan manipüle edilmiyor, her şey saf JS alanında çözülüyor.
private fun initializeWasmBridge() {
    js("""
        if (!window.tastyCallbacks) {
            window.tastyCallbacks = {};
        }

        window.tastyTrigger = function(id) {
            if (window.tastyCallbacks && typeof window.tastyCallbacks[id] === 'function') {
                window.tastyCallbacks[id]();
            } else {
                console.error("Buton fonksiyonu bulunamadı: " + id);
            }
        };

        // Kotlin tarafındaki lambdayı güvenle global objeye kaydeden yardımcı JS fonksiyonu
        window.registerTastyCallback = function(id, onClickLambda) {
            if (!window.tastyCallbacks) {
                window.tastyCallbacks = {};
            }
            window.tastyCallbacks[id] = onClickLambda;
        };
    """)
}

object TastyButtonRegistry {
    private val callbacks = mutableMapOf<String, () -> Unit>()

    init {
        // Altyapıyı ve JS fonksiyonlarını hazırlıyoruz
        initializeWasmBridge()
    }

    fun register(id: String, onClick: () -> Unit) {
        // Kendi içimizdeki Kotlin haritasına kaydediyoruz
        callbacks[id] = onClick

        // 2. ADIM: Kotlin/Wasm'da 'asDynamic' kullanmak yerine, yukarıda yazdığımız
        // güvenli JS fonksiyonuna id ve onClick lambdasını doğrudan paslıyoruz!
        // Derleyici buradaki parametre geçişine bayılır, asla hata vermez.
        registerCallbackInJs(id, onClick)
    }

    fun trigger(id: String) {
        callbacks[id]?.invoke()
    }
}

// 3. ADIM: Kotlin'deki lambda fonksiyonunu JS tarafına güvenle fırlatan ara fonksiyon
private fun registerCallbackInJs(id: String, onClick: () -> Unit) {
    js("window.registerTastyCallback(id, onClick);")
}
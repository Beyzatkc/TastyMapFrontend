package org.beem.tastymap.core.local

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import kotlinx.browser.document

// 1. Wasm için JavaScript fonksiyonumuzu dosyada "external" olarak tanımlıyoruz
@JsFun("""
(lang) => {
    Object.defineProperty(window.navigator, 'language', {
        get: function() { return lang; },
        configurable: true
    });
    Object.defineProperty(window.navigator, 'languages', {
        get: function() { return [lang]; },
        configurable: true
    });
}
""")
private external fun overrideBrowserLanguage(lang: String)

@Composable
actual fun ChangeAppLanguage(languageCode: String) {
    SideEffect {
        // 1. HTML dil etiketini güncelle (SEO ve Erişilebilirlik için)
        document.documentElement?.setAttribute("lang", languageCode)

        // 2. Compose Wasm'ın dili algılaması için tarayıcı dilini üstteki JsFun ile ez
        try {
            overrideBrowserLanguage(languageCode)
        } catch (e: Throwable) {
            println("Wasm dil değişimi başarısız: ${e.message}")
        }
    }
}
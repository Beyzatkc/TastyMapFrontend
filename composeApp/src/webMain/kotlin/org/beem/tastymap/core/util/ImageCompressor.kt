package org.beem.tastymap.core.util
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.js.JsString
import kotlin.js.Promise

actual object ImageCompressor {

    @OptIn(ExperimentalEncodingApi::class, ExperimentalWasmJsInterop::class)
    actual suspend fun compress(
        bytes: ByteArray,
        maxWidth: Int,
        maxHeight: Int,
        quality: Int
    ): ByteArray {
        if (bytes.isEmpty()) return bytes

        return runCatching {
            // 1. Kotlin ByteArray -> Base64 Data URL
            val base64Input = Base64.Default.encode(bytes)
            val dataUrl = "data:image/jpeg;base64,$base64Input"

            // 2. JS motorunda Canvas ile resmi küçült ve sıkıştır
            val qualityDecimal = quality.toDouble() / 100.0
            val jsPromise = compressImageWasmJs(dataUrl, maxWidth, maxHeight, qualityDecimal)

            // 3. JS Promise sonucunu asenkron olarak bekle
            val compressedDataUrl = jsPromise.await().toString()

            // 4. Base64 Data URL -> Kotlin ByteArray
            val base64Output = compressedDataUrl.substringAfter(",")
            Base64.Default.decode(base64Output)
        }.getOrDefault(bytes)
    }
}

// Kotlin/Wasm için JS köprüsü (DOM Canvas işlemleri burada gerçekleşir)
@JsFun("""
    (dataUrl, maxWidth, maxHeight, quality) => {
        return new Promise((resolve, reject) => {
            const img = new Image();
            img.onload = () => {
                let width = img.naturalWidth || img.width;
                let height = img.naturalHeight || img.height;

               
                const ratio = Math.min(maxWidth / width, maxHeight / height);
                if (ratio < 1.0) {
                    width = Math.round(width * ratio);
                    height = Math.round(height * ratio);
                }

             
                const canvas = document.createElement('canvas');
                canvas.width = width;
                canvas.height = height;

                const ctx = canvas.getContext('2d');
                ctx.drawImage(img, 0, 0, width, height);

              
                resolve(canvas.toDataURL('image/jpeg', quality));
            };
            img.onerror = (e) => reject(new Error('Resim yuklenemedi'));
            img.src = dataUrl;
        });
    }
""")
private external fun compressImageWasmJs(
    dataUrl: String,
    maxWidth: Int,
    maxHeight: Int,
    quality: Double
): Promise<JsString>
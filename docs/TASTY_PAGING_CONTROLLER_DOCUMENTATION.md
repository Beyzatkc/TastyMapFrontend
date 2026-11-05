# TastyPagingController Kullanım ve Entegrasyon Rehberi

## 1. Amaç

`TastyPagingController`, sayfalı veri yükleme işlemlerini tek bir yerde yönetmek için geliştirilmiş generic bir Kotlin sınıfıdır.

Bu sınıfın temel görevi:

- Verileri sayfa sayfa yüklemek
- Aynı anda birden fazla yükleme isteği gönderilmesini engellemek
- Yeni gelen verileri mevcut listeye eklemek
- Tekrarlanan kayıtları filtrelemek
- Yükleme, hata ve sayfa sonu durumlarını tek bir state üzerinden yayınlamak
- Gerektiğinde bütün sayfalama durumunu sıfırlamak

Bu controller; yorumlar, ürünler, kullanıcılar, restoranlar, mesajlar veya başka herhangi bir liste tipi için kullanılabilir.

---

## 2. Kaynak Kod

```kotlin
package org.beem.tastymap.core.paging

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TastyPagingController<T, K>(
    private val pageSize: Int = 5,
    private val scope: CoroutineScope,
    private val fetchPage: suspend (page: Int, pageSize: Int) -> List<T>,
    private val itemKeySelector: (T) -> K,
) {
    private val _state = MutableStateFlow(TastyPagingState<T>())
    val state: StateFlow<TastyPagingState<T>> = _state.asStateFlow()

    private var activeJob: Job? = null

    fun loadNextPage() {
        val currentState = _state.value
        if (currentState.isLoading || currentState.isEndReached) return

        _state.update { it.copy(isLoading = true, error = null) }

        activeJob = scope.launch {
            try {
                val newItems = fetchPage(
                    currentState.currentPage,
                    pageSize
                )

                val updatedItems = currentState.items + newItems
                val uniqueList = updatedItems.distinctBy(itemKeySelector)

                _state.update { previousState ->
                    previousState.copy(
                        items = uniqueList,
                        currentPage = previousState.currentPage + 1,
                        isLoading = false,
                        isEndReached = newItems.isEmpty() ||
                            newItems.size < pageSize
                    )
                }
            } catch (exception: Exception) {
                _state.update { previousState ->
                    previousState.copy(
                        isLoading = false,
                        error = exception.message
                            ?: "Bilinmeyen bir hata oluştu"
                    )
                }
            }
        }
    }

    fun reset() {
        activeJob?.cancel()
        activeJob = null
        _state.value = TastyPagingState()
    }
}
```

Controller şu state modelini kullanır:

```kotlin
package org.beem.tastymap.core.paging

data class TastyPagingState<T>(
    val items: List<T> = emptyList(),
    val currentPage: Int = 0,
    val isLoading: Boolean = false,
    val isEndReached: Boolean = false,
    val error: String? = null
)
```

---

## 3. Generic Tipler

Sınıf iki generic tip alır:

```kotlin
TastyPagingController<T, K>
```

### `T`

Listede tutulacak veri modelinin tipidir.

Örnekler:

```kotlin
TastyPagingController<ReviewItem, Long>
TastyPagingController<Product, String>
TastyPagingController<User, Int>
```

Burada:

- `ReviewItem`
- `Product`
- `User`

gibi tipler `T` yerine geçer.

### `K`

Her öğenin benzersiz kimliğinin tipidir.

Örnekler:

```kotlin
itemKeySelector = { review -> review.id }
```

Eğer `review.id` bir `Long` ise:

```kotlin
TastyPagingController<ReviewItem, Long>
```

Eğer `product.id` bir `String` ise:

```kotlin
TastyPagingController<Product, String>
```

`K`, duplicate kontrolü için kullanılır.

---

## 4. Constructor Parametreleri

## `pageSize`

```kotlin
private val pageSize: Int = 5
```

Her API isteğinde kaç öğe alınacağını belirler.

Örnek:

```kotlin
pageSize = 20
```

Bu durumda controller her istekte backend'e `size = 20` gönderir.

Backend ile frontend aynı sayfa boyutu mantığını kullanmalıdır.

---

## `scope`

```kotlin
private val scope: CoroutineScope
```

Ağ isteğinin çalışacağı coroutine scope'tur.

Android ViewModel örneği:

```kotlin
scope = viewModelScope
```

Voyager ScreenModel örneği:

```kotlin
scope = screenModelScope
```

Bağımsız bir sınıfta özel scope örneği:

```kotlin
scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
```

Controller'ın yaşam döngüsü, verilen scope'un yaşam döngüsüne bağlıdır.

Scope iptal edilirse controller'ın başlattığı coroutine işlemleri de iptal edilir.

---

## `fetchPage`

```kotlin
private val fetchPage:
    suspend (page: Int, pageSize: Int) -> List<T>
```

Controller'ın veriyi nasıl yükleyeceğini bilmesini sağlayan suspend fonksiyondur.

Controller doğrudan repository veya API sınıfını tanımaz. Bunun yerine dışarıdan bir fonksiyon alır.

Örnek:

```kotlin
fetchPage = { page, pageSize ->
    repository.getProducts(
        page = page,
        size = pageSize
    )
}
```

Bu fonksiyonun başarılı durumda `List<T>` döndürmesi gerekir.

Örnek:

```kotlin
List<Product>
List<ReviewItem>
List<User>
```

Hata oluştuğunda boş liste dönmek yerine exception fırlatılması tavsiye edilir.

Doğru:

```kotlin
fetchPage = { page, pageSize ->
    when (
        val result = repository.getProducts(page, pageSize)
    ) {
        is Result.Success -> result.data

        is Result.Error -> {
            throw Exception(result.message)
        }
    }
}
```

Riskli kullanım:

```kotlin
is Result.Error -> emptyList()
```

Boş liste dönülürse controller bunu hata olarak değil, veri bitti olarak yorumlayabilir.

---

## `itemKeySelector`

```kotlin
private val itemKeySelector: (T) -> K
```

Her öğenin benzersiz anahtarını seçer.

Örnek:

```kotlin
itemKeySelector = { item -> item.id }
```

Kısa kullanım:

```kotlin
itemKeySelector = { it.id }
```

Controller, eski ve yeni öğeleri birleştirdikten sonra şu işlemi yapar:

```kotlin
updatedItems.distinctBy(itemKeySelector)
```

Bu sayede aynı anahtara sahip öğeler listede yalnızca bir kez tutulur.

Önemli:

`itemKeySelector = { it.id }` yalnızca aynı ID'ye sahip kayıtları duplicate kabul eder.

İçeriği aynı fakat ID'si farklı iki kayıt, controller tarafından farklı öğeler olarak kabul edilir.

Örnek:

```text
id = 10, content = "Güzel mekan"
id = 25, content = "Güzel mekan"
```

Bu iki kayıt aynı içeriğe sahip olsa bile ID değerleri farklı olduğu için ikisi de listede kalır.

---

## 5. State Yapısı

Controller state'i şu şekilde dışarı açar:

```kotlin
private val _state =
    MutableStateFlow(TastyPagingState<T>())

val state: StateFlow<TastyPagingState<T>> =
    _state.asStateFlow()
```

### `_state`

Controller'ın kendi içinde değiştirebildiği state'tir.

Dışarıdan erişilemez.

### `state`

UI veya başka katmanların okuyabildiği salt okunur `StateFlow` nesnesidir.

Dış katmanlar state'i gözlemleyebilir fakat doğrudan değiştiremez.

Bu yaklaşım state yönetimini controller içinde güvenli tutar.

---

## 6. TastyPagingState Alanları

## `items`

```kotlin
val items: List<T> = emptyList()
```

Şu ana kadar yüklenen bütün öğeleri içerir.

İlk durumda boş listedir.

Her başarılı sayfa isteğinden sonra yeni veriler bu listeye eklenir.

---

## `currentPage`

```kotlin
val currentPage: Int = 0
```

Bir sonraki istekte gönderilecek sayfa numarasını tutar.

İlk istek:

```text
page = 0
```

İlk başarılı istekten sonra:

```text
currentPage = 1
```

İkinci başarılı istekten sonra:

```text
currentPage = 2
```

Sayfa numarası yalnızca başarılı yüklemeden sonra artırılır.

---

## `isLoading`

```kotlin
val isLoading: Boolean = false
```

Aktif bir sayfa yükleme işlemi olup olmadığını belirtir.

İstek başlamadan önce:

```text
isLoading = false
```

İstek başladığında:

```text
isLoading = true
```

İstek başarılı veya hatalı tamamlandığında:

```text
isLoading = false
```

Bu alan UI'da progress indicator göstermek için kullanılabilir.

Aynı zamanda controller'ın arka arkaya duplicate istek göndermesini engeller.

---

## `isEndReached`

```kotlin
val isEndReached: Boolean = false
```

Yüklenecek başka sayfa kalıp kalmadığını belirtir.

Controller şu durumda sayfa sonuna ulaşıldığını kabul eder:

```kotlin
newItems.isEmpty() || newItems.size < pageSize
```

Örnek:

```text
pageSize = 20
newItems.size = 20
```

Sonraki sayfa olabilir:

```text
isEndReached = false
```

Örnek:

```text
pageSize = 20
newItems.size = 7
```

Son sayfa kabul edilir:

```text
isEndReached = true
```

Örnek:

```text
newItems.size = 0
```

Veri tamamen bitmiştir:

```text
isEndReached = true
```

Bu davranış backend'in son sayfada `pageSize` değerinden daha az öğe döndürdüğü varsayımına dayanır.

---

## `error`

```kotlin
val error: String? = null
```

Son yükleme işleminde oluşan hata mesajını tutar.

Başarılı veya yeni bir yükleme başladığında:

```text
error = null
```

Hata oluştuğunda:

```text
error = exception.message
```

UI bu alanı kullanarak kullanıcıya hata mesajı gösterebilir.

---

## 7. `loadNextPage()` Metodu

```kotlin
fun loadNextPage()
```

Bir sonraki sayfayı yükler.

Metodun çalışma sırası aşağıdaki gibidir.

### 1. Mevcut state alınır

```kotlin
val currentState = _state.value
```

Bu snapshot, isteğin hangi sayfadan başlayacağını ve mevcut öğeleri belirler.

---

### 2. Gereksiz istekler engellenir

```kotlin
if (
    currentState.isLoading ||
    currentState.isEndReached
) return
```

Eğer bir istek zaten çalışıyorsa yeni istek başlamaz.

Eğer bütün sayfalar yüklendiyse yeni istek başlamaz.

Bu kontrol özellikle scroll sonunda aynı callback'in birden fazla kez çağrıldığı UI yapılarında önemlidir.

---

### 3. Yükleme state'i aktif edilir

```kotlin
_state.update {
    it.copy(
        isLoading = true,
        error = null
    )
}
```

Yeni istek başladığında:

- `isLoading` true olur
- Önceki hata temizlenir

---

### 4. Coroutine başlatılır

```kotlin
activeJob = scope.launch {
    // ...
}
```

API isteği verilen `CoroutineScope` içinde çalıştırılır.

Job referansı `activeJob` içinde tutulur.

Bu referans daha sonra `reset()` çağrısında iptal edilebilir.

---

### 5. İlgili sayfa yüklenir

```kotlin
val newItems = fetchPage(
    currentState.currentPage,
    pageSize
)
```

Controller dışarıdan verilen `fetchPage` fonksiyonunu çağırır.

Örnek:

```text
currentPage = 0
pageSize = 20
```

İstek:

```text
fetchPage(0, 20)
```

---

### 6. Yeni ve eski veriler birleştirilir

```kotlin
val updatedItems =
    currentState.items + newItems
```

Önceki öğeler silinmez.

Yeni gelen öğeler listenin sonuna eklenir.

---

### 7. Duplicate kayıtlar temizlenir

```kotlin
val uniqueList =
    updatedItems.distinctBy(itemKeySelector)
```

Aynı anahtara sahip öğelerden yalnızca ilki korunur.

---

### 8. State güncellenir

```kotlin
_state.update { previousState ->
    previousState.copy(
        items = uniqueList,
        currentPage =
            previousState.currentPage + 1,
        isLoading = false,
        isEndReached =
            newItems.isEmpty() ||
            newItems.size < pageSize
    )
}
```

Başarılı yükleme sonunda:

- Liste güncellenir
- Sayfa numarası artırılır
- Loading kapatılır
- Sayfa sonuna ulaşılıp ulaşılmadığı hesaplanır

---

### 9. Hata yakalanır

```kotlin
catch (exception: Exception) {
    _state.update { previousState ->
        previousState.copy(
            isLoading = false,
            error = exception.message
                ?: "Bilinmeyen bir hata oluştu"
        )
    }
}
```

Hata durumunda:

- Mevcut liste korunur
- Sayfa numarası artırılmaz
- Loading kapatılır
- Hata mesajı state'e yazılır

Bu nedenle aynı sayfa daha sonra tekrar denenebilir.

---

## 8. `reset()` Metodu

```kotlin
fun reset()
```

Controller'ı ilk oluşturulduğu duruma döndürür.

### Aktif iş iptal edilir

```kotlin
activeJob?.cancel()
```

Devam eden istek varsa iptal edilir.

### Job referansı temizlenir

```kotlin
activeJob = null
```

### State sıfırlanır

```kotlin
_state.value = TastyPagingState()
```

Sonuç:

```text
items = []
currentPage = 0
isLoading = false
isEndReached = false
error = null
```

`reset()` şu durumlarda kullanılabilir:

- Farklı bir kullanıcı seçildiğinde
- Farklı bir kategori açıldığında
- Farklı bir restoran veya ürün detayına geçildiğinde
- Filtre değiştiğinde
- Arama kelimesi değiştiğinde
- Ekran kapatıldığında
- Liste baştan yüklenmek istendiğinde

---

## 9. Temel Kullanım Örneği

Örnek model:

```kotlin
data class Product(
    val id: Long,
    val name: String
)
```

Controller oluşturma:

```kotlin
private val pagingController =
    TastyPagingController<Product, Long>(
        pageSize = 20,
        scope = viewModelScope,
        fetchPage = { page, pageSize ->
            repository.getProducts(
                page = page,
                size = pageSize
            )
        },
        itemKeySelector = { product ->
            product.id
        }
    )
```

State'i dışarı açma:

```kotlin
val pagingState:
    StateFlow<TastyPagingState<Product>> =
    pagingController.state
```

İlk sayfayı yükleme:

```kotlin
fun loadInitialProducts() {
    pagingController.loadNextPage()
}
```

Sonraki sayfayı yükleme:

```kotlin
fun loadMoreProducts() {
    pagingController.loadNextPage()
}
```

Sıfırlama:

```kotlin
fun resetProducts() {
    pagingController.reset()
}
```

---

## 10. Repository Sonucu Hata Tipi İçeriyorsa

Repository doğrudan `List<T>` döndürmüyorsa sonuç controller'ın beklediği formata çevrilmelidir.

Örnek:

```kotlin
fetchPage = { page, pageSize ->
    when (
        val result = repository.getProducts(
            page = page,
            size = pageSize
        )
    ) {
        is ResultWrapper.Success -> {
            result.data.items.orEmpty()
        }

        is ResultWrapper.Error -> {
            throw Exception(
                result.message
                    ?: "Ürünler yüklenemedi"
            )
        }
    }
}
```

Başarılı sonuç:

```kotlin
List<Product>
```

Hatalı sonuç:

```kotlin
throw Exception(...)
```

şeklinde controller'a aktarılmalıdır.

Böylece controller hata ile gerçek sayfa sonunu birbirinden ayırabilir.

---

## 11. State Nasıl Gözlemlenir?

## Compose

```kotlin
val pagingState by
    pagingController.state.collectAsState()
```

Daha sonra:

```kotlin
when {
    pagingState.items.isEmpty() &&
        pagingState.isLoading -> {
        InitialLoading()
    }

    pagingState.items.isEmpty() &&
        pagingState.error != null -> {
        ErrorContent(
            message = pagingState.error
                ?: "Bir hata oluştu"
        )
    }

    else -> {
        ItemList(
            items = pagingState.items
        )
    }
}
```

Listenin altında yeni sayfa loading'i:

```kotlin
if (
    pagingState.isLoading &&
    pagingState.items.isNotEmpty()
) {
    LoadingMoreIndicator()
}
```

---

## ViewModel veya ScreenModel üzerinden

Controller state'i doğrudan dışarı açılabilir:

```kotlin
val pagingState = pagingController.state
```

Alternatif olarak başka bir state'e aktarılabilir:

```kotlin
private val _uiState =
    MutableStateFlow(
        TastyPagingState<Product>()
    )

val uiState =
    _uiState.asStateFlow()

private val collectJob =
    scope.launch {
        pagingController.state.collect {
            newState ->
            _uiState.value = newState
        }
    }
```

Controller state'i doğrudan kullanılabiliyorsa ikinci bir StateFlow oluşturmak zorunlu değildir.

---

## 12. Scroll Sonunda Yeni Sayfa Yükleme

Controller UI teknolojisini bilmez.

UI, listenin sonuna gelindiğini algıladığında yalnızca şunu çağırmalıdır:

```kotlin
pagingController.loadNextPage()
```

Controller kendi içinde şu kontrolleri yaptığı için callback birden fazla kez tetiklense bile aynı anda yeni istek başlatmaz:

```kotlin
if (
    currentState.isLoading ||
    currentState.isEndReached
) return
```

Generic Compose örneği:

```kotlin
LazyColumn {
    items(
        items = pagingState.items,
        key = { item -> item.id }
    ) { item ->
        ProductRow(item)
    }

    item {
        if (pagingState.isLoading) {
            LoadingMoreIndicator()
        }
    }
}
```

Son öğeye yaklaşıldığında:

```kotlin
LaunchedEffect(
    listState,
    pagingState.items.size
) {
    snapshotFlow {
        listState.layoutInfo
            .visibleItemsInfo
            .lastOrNull()
            ?.index
    }.collect { lastVisibleIndex ->
        val lastItemIndex =
            pagingState.items.lastIndex

        if (
            lastVisibleIndex != null &&
            lastVisibleIndex >= lastItemIndex - 2
        ) {
            pagingController.loadNextPage()
        }
    }
}
```

Bu örnekte kullanıcı son iki öğeye yaklaştığında yeni sayfa istenir.

---

## 13. Retry İşlemi

Controller'da ayrı bir `retry()` metodu bulunmaz.

Hata durumunda `currentPage` artırılmadığı için tekrar:

```kotlin
pagingController.loadNextPage()
```

çağrılması aynı sayfayı yeniden dener.

Örnek:

```kotlin
if (pagingState.error != null) {
    RetryButton(
        onClick = {
            pagingController.loadNextPage()
        }
    )
}
```

---

## 14. Entegrasyon Sözleşmesi

Bu sınıfı başka bir projeye entegre eden geliştirici veya yapay zekâ aşağıdaki kurallara uymalıdır.

### Zorunlu kurallar

1. `fetchPage`, başarılı durumda mutlaka `List<T>` döndürmelidir.
2. Ağ veya repository hatası `exception` olarak controller'a aktarılmalıdır.
3. `itemKeySelector`, gerçekten benzersiz ve kararlı bir alan seçmelidir.
4. Controller'a verilen scope uygun yaşam döngüsüne bağlı olmalıdır.
5. İlk sayfa otomatik yüklenmez; gerektiğinde `loadNextPage()` çağrılmalıdır.
6. Veri kümesi değiştiğinde `reset()` çağrılmalıdır.
7. Backend sayfa indeksinin 0'dan mı 1'den mi başladığı kontrol edilmelidir.
8. `pageSize` frontend ve backend mantığıyla uyumlu olmalıdır.

---

## 15. Backend 1 Tabanlı Sayfa Kullanıyorsa

Bu controller varsayılan olarak ilk istekte:

```text
page = 0
```

gönderir.

Backend ilk sayfayı `1` kabul ediyorsa adapter uygulanmalıdır:

```kotlin
fetchPage = { page, pageSize ->
    repository.getProducts(
        page = page + 1,
        size = pageSize
    )
}
```

Controller'ın kendi `currentPage` alanını değiştirmek yerine dönüşüm `fetchPage` içinde yapılabilir.

---

## 16. Duplicate Anahtarı Seçimi

En güvenli kullanım:

```kotlin
itemKeySelector = { it.id }
```

Ancak ID gerçekten benzersiz olmalıdır.

Birden fazla alan gerekiyorsa birleşik anahtar kullanılabilir:

```kotlin
itemKeySelector = {
    "${it.source}-${it.id}"
}
```

Veya:

```kotlin
itemKeySelector = {
    it.placeId to it.id
}
```

Bu durumda `K`, `Pair<String, Long>` olabilir.

İçeriğe göre duplicate filtrelemek mümkündür:

```kotlin
itemKeySelector = {
    Triple(
        it.name,
        it.content,
        it.createdAt
    )
}
```

Ancak bu yaklaşım dikkatli kullanılmalıdır. İki farklı gerçek kayıt aynı içeriğe sahip olabilir.

En doğru benzersizlik kuralı domain modeline göre belirlenmelidir.

---

## 17. Hata ve Sayfa Sonu Ayrımı

Aşağıdaki iki durum birbirinden farklıdır.

### Gerçek sayfa sonu

Backend başarılı cevap verir:

```kotlin
emptyList()
```

Controller:

```text
isEndReached = true
```

yapar.

### Ağ veya sunucu hatası

Repository hata döndürür:

```kotlin
throw Exception("Sunucuya ulaşılamadı")
```

Controller:

```text
error = "Sunucuya ulaşılamadı"
isEndReached = false
```

durumunu korur.

Bu ayrım retry mekanizmasının doğru çalışması için önemlidir.

---

## 18. Yaşam Döngüsü

Controller kendi scope'unu oluşturmaz.

Bu nedenle controller'ın ne kadar yaşayacağı verilen scope'a bağlıdır.

ViewModel içinde:

```kotlin
scope = viewModelScope
```

Voyager ScreenModel içinde:

```kotlin
scope = screenModelScope
```

Ekran kapandığında scope iptal oluyorsa aktif işlemler de otomatik iptal edilir.

Farklı bir veri kümesine geçerken ayrıca:

```kotlin
pagingController.reset()
```

çağrılmalıdır.

---

## 19. Bilinen Davranışlar ve Sınırlar

### Yalnızca tek aktif yükleme mantığı vardır

Controller aynı anda bir sayfa isteği çalıştırmak üzere tasarlanmıştır.

Paralel olarak birden fazla sayfa yüklemez.

### Sayfa numarası başarılı istekten sonra artar

Hata oluşursa aynı sayfa tekrar denenebilir.

### Son sayfa boyuta göre anlaşılır

Controller toplam sayfa sayısını veya backend'in `hasNext` değerini kullanmaz.

Şu varsayımı kullanır:

```kotlin
newItems.size < pageSize
```

Backend tam dolu son sayfa döndürüyorsa, controller bir sonraki boş sayfayı da istemek zorunda kalabilir.

Bu durumda sonraki boş cevapta `isEndReached = true` olur.

### Refresh özelliği ayrı değildir

Tam yenileme için:

```kotlin
pagingController.reset()
pagingController.loadNextPage()
```

kullanılır.

### Liste yalnızca bellekte tutulur

Controller disk cache veya database cache yönetmez.

---

## 20. Önerilen Dosya Yapısı

```text
core/
└── paging/
    ├── TastyPagingController.kt
    └── TastyPagingState.kt
```

Önerilen paket:

```kotlin
package org.beem.tastymap.core.paging
```

Başka projeye taşınırken yalnızca proje namespace'i değiştirilmelidir.

Örnek:

```kotlin
package com.example.app.core.paging
```

---

## 21. Yapay Zekâ İçin Entegrasyon Talimatı

Bu doküman bir yapay zekâya verildiğinde aşağıdaki görev metni kullanılabilir:

```text
Bu projeye TastyPagingController sınıfını entegre et.

Önce projenin mimarisini, coroutine scope kullanımını,
repository dönüş tipini, backend sayfa indeksini ve listelenecek
modelin benzersiz anahtarını incele.

TastyPagingController'ın kaynak kodunu gereksiz şekilde değiştirme.

Projeye uygun şekilde:

1. TastyPagingController<T, K> instance'ını oluştur.
2. Doğru CoroutineScope'u ver.
3. Repository sonucunu List<T> döndüren fetchPage fonksiyonuna dönüştür.
4. Hatalarda emptyList dönmek yerine exception fırlat.
5. itemKeySelector için gerçek benzersiz alanı kullan.
6. Controller state'ini UI tarafından gözlemlenebilir hale getir.
7. İlk yükleme için loadNextPage çağrısı ekle.
8. Scroll sonu veya load-more callback'inde loadNextPage çağır.
9. Veri kaynağı değiştiğinde reset çağır.
10. İlk loading, sonraki sayfa loading, empty state ve error
    durumlarını UI'da ayrı ayrı yönet.

Entegrasyon sonunda değiştirilen dosyaları ve veri akışını açıkla.
```

---

## 22. Hızlı Referans

Controller oluşturma:

```kotlin
val controller =
    TastyPagingController<Item, Long>(
        pageSize = 20,
        scope = scope,
        fetchPage = { page, size ->
            repository.loadItems(page, size)
        },
        itemKeySelector = { it.id }
    )
```

İlk sayfa:

```kotlin
controller.loadNextPage()
```

Sonraki sayfa:

```kotlin
controller.loadNextPage()
```

Retry:

```kotlin
controller.loadNextPage()
```

Sıfırlama:

```kotlin
controller.reset()
```

State:

```kotlin
controller.state
```

Liste:

```kotlin
controller.state.value.items
```

Loading:

```kotlin
controller.state.value.isLoading
```

Hata:

```kotlin
controller.state.value.error
```

Sayfa sonu:

```kotlin
controller.state.value.isEndReached
```

---

## 23. Özet

`TastyPagingController` şu sorumlulukları üstlenir:

- Sayfa numarasını takip eder
- Yükleme durumunu yönetir
- Aynı anda duplicate istekleri engeller
- Yeni öğeleri mevcut listeye ekler
- Seçilen anahtara göre duplicate öğeleri temizler
- Sayfa sonunu belirler
- Hata mesajını state'e taşır
- Aktif işi ve bütün state'i sıfırlayabilir

Controller'ın sorumluluğu olmayan konular:

- API endpoint tanımlamak
- Repository oluşturmak
- UI çizmek
- Scroll sonunu algılamak
- Disk cache yönetmek
- Backend duplicate kayıtlarını düzeltmek
- Domain modelinin benzersiz anahtarına karar vermek

Bu ayrım sayesinde sınıf farklı projelerde ve farklı veri modellerinde yeniden kullanılabilir.

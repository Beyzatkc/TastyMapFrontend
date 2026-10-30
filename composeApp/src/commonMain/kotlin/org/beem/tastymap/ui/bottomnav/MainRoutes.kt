import kotlinx.serialization.Serializable

// Ana uygulama kabuğu (Tüm alt sekmeleri kapsar)
@Serializable data object MainRoute

// --- MAP SEKMESİ ---
@Serializable data object MapGraphRoute
@Serializable data object MapTabRoute // Harita ana ekranı

// --- ARAMA SEKMESİ ---
@Serializable data object SearchGraphRoute
@Serializable data object SearchTabRoute // Arama ana ekranı

// --- AI SEKMESİ ---
@Serializable data object AIGraphRoute
@Serializable data object AITabRoute // AI ana ekranı

// --- YÜKLEME SEKMESİ ---
@Serializable data object UploadGraphRoute
@Serializable data object UploadTabRoute // Yükleme ana ekranı

// --- PROFİL SEKMESİ ---
@Serializable data object ProfileGraphRoute
@Serializable data object ProfileTabRoute // Kendi profilimizin ana ekranı
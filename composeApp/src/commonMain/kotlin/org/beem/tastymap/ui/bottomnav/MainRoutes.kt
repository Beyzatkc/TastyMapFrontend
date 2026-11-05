import kotlinx.serialization.Serializable

// Ana uygulama kabuğu (Tüm alt sekmeleri kapsar)
@Serializable data object MainRoute

// --- MAP SEKMESİ ---
@Serializable data object MapGraphRoute
@Serializable data object map // Harita ana ekranı

// --- ARAMA SEKMESİ ---
@Serializable data object SearchGraphRoute
@Serializable data object search // Arama ana ekranı

// --- AI SEKMESİ ---
@Serializable data object AIGraphRoute
@Serializable data object AI // AI ana ekranı

// --- YÜKLEME SEKMESİ ---
@Serializable data object UploadGraphRoute
@Serializable data object upload // Yükleme ana ekranı

// --- PROFİL SEKMESİ ---
@Serializable data object ProfileGraphRoute
@Serializable data object my // Kendi profilimizin ana ekranı
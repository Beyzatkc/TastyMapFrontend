package org.beem.tastymap.ui.profile.myprofile.settings.support

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.beem.tastymap.core.util.ToastManager
import org.beem.tastymap.data.model.support.SupportCategory
import org.beem.tastymap.ui.components.TastyButton
import org.beem.tastymap.ui.components.TastyTextField
import org.beem.tastymap.ui.components.responsiveContentWidth
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

data class FaqItem(
    val question: String,
    val answer: String
)

private fun SupportCategory.toDisplayLabel(): String = when (this) {
    SupportCategory.BUG -> "Hata Bildir"
    SupportCategory.AI_NUTRITION -> "AI & Beslenme"
    SupportCategory.ACCOUNT -> "Hesap & Güvenlik"
    SupportCategory.SUGGESTION -> "Öneri"
    SupportCategory.OTHER -> "Diğer"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactSupportScreen(
    navController: NavController,
    appVersion: String = "v1.0.0"
) {
    val supportViewModel = koinViewModel<SupportViewModel>()
    val uiState by supportViewModel.uiState.collectAsState()

    val customColors = LocalCustomColors.current
    val uriHandler = LocalUriHandler.current
    val clipboardManager = LocalClipboardManager.current

    var selectedCategory by remember { mutableStateOf(SupportCategory.BUG) }
    var messageText by remember { mutableStateOf("") }
    var expandedFaqIndex by remember { mutableStateOf<Int?>(null) }

    val supportEmail = "beemdevops@gmail.com"
    val maxCharCount = 500

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            ToastManager.show("Destek talebiniz alındı. Teşekkürler!")
            messageText = ""
            supportViewModel.clearMessages()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            ToastManager.show(message)
            supportViewModel.clearMessages()
        }
    }

    val faqList = remember {
        listOf(
            FaqItem(
                question = "AI önerileri alerji ve beslenme tercihlerimi nasıl kullanıyor?",
                answer = "Ayarlar > Beslenme Tercihleri kısmından belirlediğiniz kısıtlamalar, mekan ve yemek önerilerinde otomatik olarak filtrelenir."
            ),
            FaqItem(
                question = "Gizli hesap özelliğini açtığımda neler değişir?",
                answer = "Hesabınız gizli moda alındığında profiliniz, ziyaret geçmişiniz ve paylaşımlarınız yalnızca onayladığınız kişiler tarafından görülebilir."
            ),
            FaqItem(
                question = "Hesabıma başka bir cihazdan giriş yapıldığını nasıl anlarım?",
                answer = "Ayarlar > Aktif Cihazlar ekranından hesabınızın açık olduğu tüm cihazları görebilirsiniz."
            ),
            FaqItem(
                question = "Hesabımı sildiğimde verilerim ne olur?",
                answer = "Hesap silme talebi oluşturduğunuzda hesabınız 30 gün boyunca askıya alınır. Bu süre içinde tekrar giriş yaparsanız silme işlemi otomatik olarak iptal edilir. 30 gün sonunda giriş yapmazsanız ziyaret geçmişiniz, favorileriniz ve tüm profil verileriniz sistemden kalıcı olarak silinir."
            )
        )
    }

    Scaffold(
        containerColor = customColors.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Bize Ulaşın / Destek",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = customColors.textPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = customColors.textPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = customColors.background
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .responsiveContentWidth()
                    .fillMaxHeight()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // 1. Hero / Karşılama Kartı
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = customColors.surface),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = customColors.navy.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.SupportAgent,
                                        contentDescription = null,
                                        tint = customColors.navy,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Nasıl yardımcı olabiliriz?",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = customColors.textPrimary
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Destek ekibimiz mesajlarınıza genellikle 24 saat içinde dönüş yapar.",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = customColors.textSecondary
                                    )
                                )
                            }
                        }
                    }
                }

                // 2. Sıkça Sorulan Sorular (Self-Service)
                item {
                    Text(
                        text = "Sıkça Sorulan Sorular",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = customColors.textSecondary,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(start = 8.dp, bottom = 6.dp)
                    )
                    Card(
                        colors = CardDefaults.cardColors(containerColor = customColors.surface),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column {
                            faqList.forEachIndexed { index, faq ->
                                val isExpanded = expandedFaqIndex == index
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            expandedFaqIndex = if (isExpanded) null else index
                                        }
                                        .padding(horizontal = 16.dp, vertical = 14.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = faq.question,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Medium,
                                                color = customColors.textPrimary
                                            ),
                                            modifier = Modifier.weight(1f)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                            contentDescription = null,
                                            tint = customColors.textSecondary
                                        )
                                    }
                                    AnimatedVisibility(
                                        visible = isExpanded,
                                        enter = expandVertically(),
                                        exit = shrinkVertically()
                                    ) {
                                        Text(
                                            text = faq.answer,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = customColors.textSecondary,
                                                lineHeight = 18.sp
                                            ),
                                            modifier = Modifier.padding(top = 8.dp)
                                        )
                                    }
                                }
                                if (index < faqList.lastIndex) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        color = customColors.borderLight
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. Destek Talebi Formu
                item {
                    Text(
                        text = "Destek Talebi Oluştur",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = customColors.textSecondary,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(start = 8.dp, bottom = 6.dp)
                    )
                    Card(
                        colors = CardDefaults.cardColors(containerColor = customColors.surface),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Konu Seçin",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = customColors.textSecondary
                                )
                            )

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(SupportCategory.entries) { category ->
                                    val isSelected = selectedCategory == category
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { selectedCategory = category },
                                        label = {
                                            Text(
                                                text = category.toDisplayLabel(),
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                                )
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = customColors.navy,
                                            selectedLabelColor = customColors.surface,
                                            containerColor = customColors.surfaceVariant,
                                            labelColor = customColors.textPrimary
                                        ),
                                        border = null,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                }
                            }

                            Column(modifier = Modifier.fillMaxWidth()) {
                                TastyTextField(
                                    value = messageText,
                                    onValueChange = {
                                        messageText = it
                                        supportViewModel.clearMessageError()
                                    },
                                    label = "Yaşadığınız sorunu veya önerinizi detaylıca yazın...",
                                    error = uiState.messageError?.let { stringResource(it) },
                                    singleLine = false,
                                    minLines = 4,
                                    maxLines = 6
                                )

                                Text(
                                    text = "${messageText.length} / $maxCharCount",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (messageText.length > maxCharCount) customColors.error else customColors.textTertiary,
                                    modifier = Modifier
                                        .align(Alignment.End)
                                        .padding(top = 4.dp, end = 4.dp)
                                )
                            }

                            // Otomatik Metadata Bilgilendirmesi
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = customColors.textTertiary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Sürüm ($appVersion) ve cihaz bilgileri çözüm için otomatik eklenir.",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = customColors.textTertiary
                                    )
                                )
                            }

                            TastyButton(
                                text = "Talebi Gönder",
                                onClick = {
                                    supportViewModel.addSupport(
                                        category = selectedCategory,
                                        message = messageText,
                                        appVersion = appVersion
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                isLoading = uiState.isLoading,
                                enabled = messageText.isNotBlank(),
                                backcolor = customColors.navy,
                                textcolor = customColors.surface
                            )
                        }
                    }
                }

                // 4. Doğrudan E-posta Kanalı
                item {
                    Text(
                        text = "Doğrudan İletişim",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = customColors.textSecondary,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(start = 8.dp, bottom = 6.dp)
                    )
                    Card(
                        colors = CardDefaults.cardColors(containerColor = customColors.surface),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    runCatching {
                                        uriHandler.openUri("mailto:$supportEmail")
                                    }.onFailure {
                                        clipboardManager.setText(AnnotatedString(supportEmail))
                                        ToastManager.show("E-posta uygulaması bulunamadı, adres kopyalandı")
                                    }
                                }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = customColors.textSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "E-posta ile Ulaşın",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = customColors.textPrimary
                                    )
                                )
                                Text(
                                    text = supportEmail,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = customColors.textSecondary
                                    )
                                )
                            }
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(supportEmail))
                                    ToastManager.show("E-posta adresi kopyalandı")
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Kopyala",
                                    tint = customColors.textTertiary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}
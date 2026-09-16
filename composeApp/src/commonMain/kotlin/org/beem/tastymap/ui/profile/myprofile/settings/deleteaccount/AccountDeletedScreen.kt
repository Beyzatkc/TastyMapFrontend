package org.beem.tastymap.ui.profile.myprofile.settings.deleteaccount

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.beem.tastymap.ui.auth.logReg.LogRegScreen
import org.beem.tastymap.ui.components.TastyButton
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.*

class AccountDeactivatedScreen : Screen {

    @Composable
    override fun Content() {
        val customColors = LocalCustomColors.current
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            containerColor = customColors.background
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // Üst Görsel ve Bilgilendirme Alanı
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // İkon Çerçevesi
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(customColors.gourmetOrange.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = customColors.gourmetOrange,
                            modifier = Modifier.size(56.dp)
                        )
                    }

                    // Başlık ve Kısa Açıklama
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(Res.string.account_deactivated_title),
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = customColors.textPrimary
                            ),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = stringResource(Res.string.account_deactivated_subtitle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = customColors.textSecondary,
                            textAlign = TextAlign.Center
                        )
                    }

                    // 30 Gün Bilgilendirme Kartı
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = customColors.surface
                        ),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = customColors.borderLight,
                                shape = RoundedCornerShape(16.dp)
                            )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = customColors.navy,
                                modifier = Modifier.size(22.dp)
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = stringResource(Res.string.account_deactivated_info_title),
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = customColors.textPrimary
                                    )
                                )
                                Text(
                                    text = stringResource(Res.string.account_deactivated_info_desc),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = customColors.textSecondary
                                )
                            }
                        }
                    }
                }

                // Alt Buton: Giriş Ekranına Dön
                TastyButton(
                    text = stringResource(Res.string.account_deactivated_back_to_login),
                    onClick = {
                        // Kullanıcı oturumu kapandığı için Voyager ekran geçmişini tamamen temizleyip Login ekranına atar
                        navigator.replaceAll(LogRegScreen())
                    },
                    backcolor = customColors.navy,
                    textcolor = customColors.surface,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
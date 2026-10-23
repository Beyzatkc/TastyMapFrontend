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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.beem.tastymap.ui.components.TastyButton
import org.beem.tastymap.ui.components.responsiveContentWidth
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.*

@Composable
fun AccountDeactivatedScreen(navController: NavController) {
    val customColors = LocalCustomColors.current
    val deleteScreenModel = koinViewModel<DeleteAccountViewModel>()

    Scaffold(
        containerColor = customColors.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .responsiveContentWidth()
                    .fillMaxHeight()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {

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

                TastyButton(
                    text = stringResource(Res.string.account_deactivated_back_to_login),
                    onClick = {
                        deleteScreenModel.backToLogin()
                    },
                    backcolor = customColors.navy,
                    textcolor = customColors.surface,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
package org.beem.tastymap.ui.profile.myprofile.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.beem.tastymap.ui.components.responsiveContentWidth
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(navController: NavController) {
    val customColors = LocalCustomColors.current

    Scaffold(
        containerColor = customColors.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.privacy_policy_title),
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
                            contentDescription = stringResource(Res.string.settings_back_cd),
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
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = stringResource(Res.string.privacy_policy_last_updated),
                        style = MaterialTheme.typography.labelMedium,
                        color = customColors.textSecondary
                    )
                }

                item {
                    PrivacyPolicySection(
                        title = stringResource(Res.string.privacy_policy_sec1_title),
                        body = stringResource(Res.string.privacy_policy_sec1_body)
                    )
                }

                item {
                    PrivacyPolicySection(
                        title = stringResource(Res.string.privacy_policy_sec2_title),
                        body = stringResource(Res.string.privacy_policy_sec2_body)
                    )
                }

                item {
                    PrivacyPolicySection(
                        title = stringResource(Res.string.privacy_policy_sec3_title),
                        body = stringResource(Res.string.privacy_policy_sec3_body)
                    )
                }

                item {
                    PrivacyPolicySection(
                        title = stringResource(Res.string.privacy_policy_sec4_title),
                        body = stringResource(Res.string.privacy_policy_sec4_body)
                    )
                }

                item {
                    PrivacyPolicySection(
                        title = stringResource(Res.string.privacy_policy_sec5_title),
                        body = stringResource(Res.string.privacy_policy_sec5_body)
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun PrivacyPolicySection(
    title: String,
    body: String
) {
    val customColors = LocalCustomColors.current
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = customColors.textPrimary
            )
        )
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium.copy(
                lineHeight = TextUnit(20f, TextUnitType.Sp)
            ),
            color = customColors.textSecondary
        )
    }
}
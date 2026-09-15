package org.beem.tastymap.ui.profile.myprofile.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.privacy_policy_last_updated
import tastymap.composeapp.generated.resources.privacy_policy_sec1_body
import tastymap.composeapp.generated.resources.privacy_policy_sec1_title
import tastymap.composeapp.generated.resources.privacy_policy_sec2_body
import tastymap.composeapp.generated.resources.privacy_policy_sec2_title
import tastymap.composeapp.generated.resources.privacy_policy_sec3_body
import tastymap.composeapp.generated.resources.privacy_policy_sec3_title
import tastymap.composeapp.generated.resources.privacy_policy_sec4_body
import tastymap.composeapp.generated.resources.privacy_policy_sec4_title
import tastymap.composeapp.generated.resources.privacy_policy_sec5_body
import tastymap.composeapp.generated.resources.privacy_policy_sec5_title
import tastymap.composeapp.generated.resources.privacy_policy_title
import tastymap.composeapp.generated.resources.settings_back_cd

class PrivacyPolicyScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val customColors = LocalCustomColors.current
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            containerColor = customColors.background,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(Res.string.privacy_policy_title),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = customColors.textPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
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
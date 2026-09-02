package org.beem.tastymap.ui.profile.myprofile.settings.edithealth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.beem.tastymap.core.util.ToastManager
import org.beem.tastymap.data.model.health.AllergyInfo
import org.beem.tastymap.data.model.health.HealthEnum
import org.beem.tastymap.ui.profile.health.AllergyUiModel
import org.beem.tastymap.ui.profile.health.HealthScreenModel
import org.beem.tastymap.ui.profile.health.HealthUiState
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.edit_health_allergens_desc
import tastymap.composeapp.generated.resources.edit_health_back_cd
import tastymap.composeapp.generated.resources.edit_health_diabetes_desc
import tastymap.composeapp.generated.resources.edit_health_diabetes_title
import tastymap.composeapp.generated.resources.edit_health_diet_normal_desc
import tastymap.composeapp.generated.resources.edit_health_diet_normal_title
import tastymap.composeapp.generated.resources.edit_health_diet_vegan_desc
import tastymap.composeapp.generated.resources.edit_health_diet_vegan_title
import tastymap.composeapp.generated.resources.edit_health_diet_vegetarian_desc
import tastymap.composeapp.generated.resources.edit_health_diet_vegetarian_title
import tastymap.composeapp.generated.resources.edit_health_save
import tastymap.composeapp.generated.resources.edit_health_section_allergens
import tastymap.composeapp.generated.resources.edit_health_section_diet
import tastymap.composeapp.generated.resources.edit_health_section_health_status
import tastymap.composeapp.generated.resources.edit_health_success_toast
import tastymap.composeapp.generated.resources.edit_health_title

class EditHealthScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<HealthScreenModel>()
        val uiState by screenModel.healthState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(Unit) {
            screenModel.loadUserHealthProfile()
            screenModel.uiMessage.collect { message ->
                ToastManager.show(message)
            }
        }
        val successMessage = stringResource(Res.string.edit_health_success_toast)

        LaunchedEffect(uiState.isSuccess) {
            if (uiState.isSuccess) {
                ToastManager.show(successMessage)
                screenModel.resetSuccessState()
                navigator.pop()
            }
        }

        EditHealthContent(
            uiState = uiState,
            onBackClick = { navigator.pop() },
            onDiabetesToggle = { screenModel.toggleDiabetes(it) },
            onEatTypeSelect = { screenModel.selectEatType(it) },
            onAllergyToggle = { screenModel.toggleAllergy(it) },
            onSaveClick = { screenModel.updateHealthProfile() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditHealthContent(
    uiState: HealthUiState,
    onBackClick: () -> Unit = {},
    onDiabetesToggle: (Boolean) -> Unit = {},
    onEatTypeSelect: (HealthEnum) -> Unit = {},
    onAllergyToggle: (Long) -> Unit = {},
    onSaveClick: () -> Unit = {}
) {
    val customColors = LocalCustomColors.current

    Scaffold(
        containerColor = customColors.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.edit_health_title),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = customColors.textPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.edit_health_back_cd),
                            tint = customColors.textPrimary
                        )
                    }
                },
                actions = {
                    TextButton(
                        enabled = !uiState.isLoading,
                        onClick = onSaveClick
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = customColors.gourmetOrange,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = stringResource(Res.string.edit_health_save),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = customColors.gourmetOrange,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = customColors.background)
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading && uiState.initialHealthProfile == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = customColors.gourmetOrange)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                // 1. DİYABET / ŞEKER HASSASİYETİ (EMERALD VURGU)
                item {
                    SectionHeader(
                        icon = Icons.Default.HealthAndSafety,
                        title = stringResource(Res.string.edit_health_section_health_status),
                        iconTint = customColors.navy
                    )
                    Card(
                        colors = CardDefaults.cardColors(containerColor = customColors.surface),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        border = BorderStroke(1.dp, customColors.borderLight)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onDiabetesToggle(!uiState.hasDiabetes) }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(Res.string.edit_health_diabetes_title),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = customColors.textPrimary
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stringResource(Res.string.edit_health_diabetes_desc),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = customColors.textSecondary,
                                        lineHeight = 18.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Switch(
                                checked = uiState.hasDiabetes,
                                onCheckedChange = onDiabetesToggle,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = customColors.surface,
                                    checkedTrackColor = customColors.navy,

                                    uncheckedThumbColor = customColors.surface,
                                    uncheckedTrackColor = customColors.borderStrong,
                                    uncheckedBorderColor = customColors.borderStrong
                                )
                            )
                        }
                    }
                }

                // 2. BESLENME DÜZENİ (GOURMET ORANGE VURGU)
                item {
                    SectionHeader(
                        icon = Icons.Default.Restaurant,
                        title = stringResource(Res.string.edit_health_section_diet),
                        iconTint = customColors.navy
                    )
                    Card(
                        colors = CardDefaults.cardColors(containerColor = customColors.surface),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, customColors.borderLight)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            HealthEnum.entries.forEach { eatType ->
                                val isSelected = uiState.selectedEatType == eatType
                                val (title, description) = getEatTypeDetails(eatType)

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (isSelected) customColors.gourmetOrange.copy(alpha = 0.08f)
                                            else customColors.surface
                                        )
                                        .border(
                                            width = if (isSelected) 1.5.dp else 1.dp,
                                            color = if (isSelected) customColors.gourmetOrange else customColors.borderLight,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable { onEatTypeSelect(eatType) }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = null,
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = customColors.gourmetOrange,
                                            unselectedColor = customColors.textTertiary
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = title,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = customColors.textPrimary
                                            )
                                        )
                                        Text(
                                            text = description,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = customColors.textSecondary
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. ALERJEN SEÇİMİ (SOFT ORANGE CHIPS)
                item {
                    SectionHeader(
                        icon = Icons.Default.Warning,
                        title = stringResource(Res.string.edit_health_section_allergens),
                        iconTint = customColors.navy
                    )
                    Card(
                        colors = CardDefaults.cardColors(containerColor = customColors.surface),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, customColors.borderLight)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = stringResource(Res.string.edit_health_allergens_desc),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = customColors.textSecondary
                                )
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                uiState.availableAllergies.chunked(2).forEach { rowAllergies ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        for (allergy in rowAllergies) {
                                            val isSelected =
                                                uiState.selectedAllergyIds.contains(allergy.id)
                                            AllergyChip(
                                                allergy = allergy,
                                                isSelected = isSelected,
                                                onToggle = { onAllergyToggle(allergy.id) },
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                        if (rowAllergies.size == 1) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AllergyChip(
    allergy: AllergyUiModel,
    isSelected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val customColors = LocalCustomColors.current

    val bgColor = if (isSelected) customColors.gourmetOrange.copy(alpha = 0.10f) else customColors.surfaceVariant
    val textColor  = customColors.textPrimary
    val strokeColor = if (isSelected) customColors.gourmetOrange else Color.Transparent

    Surface(
        modifier = modifier
            .clip(CircleShape)
            .clickable { onToggle() },
        color = bgColor,
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, strokeColor) else null,
        shape = CircleShape
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = stringResource(allergy.nameRes),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = textColor
                )
            )
        }
    }
}

@Composable
private fun SectionHeader(
    icon: ImageVector,
    title: String,
    iconTint: Color
) {
    val customColors = LocalCustomColors.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(start = 4.dp, top = 4.dp, bottom = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = customColors.textPrimary
            )
        )
    }
}

@Composable
private fun getEatTypeDetails(eatType: HealthEnum): Pair<String, String> {
    return when (eatType) {

        HealthEnum.NORMAL -> stringResource(Res.string.edit_health_diet_normal_title) to stringResource(Res.string.edit_health_diet_normal_desc)
        HealthEnum.VEGETARIAN -> stringResource(Res.string.edit_health_diet_vegetarian_title) to stringResource(Res.string.edit_health_diet_vegetarian_desc)
        HealthEnum.VEGAN -> stringResource(Res.string.edit_health_diet_vegan_title) to stringResource(Res.string.edit_health_diet_vegan_desc)
    }
}



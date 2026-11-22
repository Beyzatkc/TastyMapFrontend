package org.beem.tastymap.ui.report

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.beem.tastymap.data.model.report.ReportReason
import org.beem.tastymap.data.model.report.ReportTargetType
import org.beem.tastymap.ui.components.TastyButton
import org.beem.tastymap.ui.components.TastyTextField
import org.beem.tastymap.ui.components.responsiveContentWidth
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.*

private fun ReportReason.toDisplayLabel(): StringResource = when (this) {
    ReportReason.SPAM -> Res.string.report_reason_spam
    ReportReason.IMPERSONATION -> Res.string.report_reason_impersonation
    ReportReason.INAPPROPRIATE_CONTENT -> Res.string.report_reason_inappropriate
    ReportReason.HARASSMENT -> Res.string.report_reason_harassment
    ReportReason.SCAM -> Res.string.report_reason_scam
    ReportReason.FALSE_INFO -> Res.string.report_reason_false_info
    ReportReason.OTHER -> Res.string.report_reason_other
}

private fun ReportTargetType.toQuestionLabel(): StringResource = when (this) {
    ReportTargetType.USER -> Res.string.report_question_user
    ReportTargetType.POST -> Res.string.report_question_post
    ReportTargetType.COMMENT -> Res.string.report_question_comment
}

private enum class ReportStep {
    SELECT_REASON,
    ADD_DETAILS,
    SUBMITTED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    targetId: Long,
    targetUsername: String? = null,
    targetType: ReportTargetType = ReportTargetType.USER,
    isSubmitting: Boolean = false,
    isSuccess: Boolean = false,
    navController: NavController,
    onClose: (() -> Unit)? = null,
    onBlockUserClick: (() -> Unit)? = null,
    onSubmitReport: (reason: ReportReason, description: String) -> Unit
) {
    val customColors = LocalCustomColors.current
    var currentStep by remember { mutableStateOf(ReportStep.SELECT_REASON) }
    var selectedReason by remember { mutableStateOf<ReportReason?>(null) }
    var description by remember { mutableStateOf("") }

    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            currentStep = ReportStep.SUBMITTED
        }
    }

    fun closeScreen() {
        if (onClose != null) onClose() else navController.popBackStack()
    }

    Scaffold(
        containerColor = customColors.background,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    if (currentStep == ReportStep.ADD_DETAILS) {
                        IconButton(onClick = { currentStep = ReportStep.SELECT_REASON }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(Res.string.report_back_cd),
                                tint = customColors.textPrimary
                            )
                        }
                    } else if (currentStep == ReportStep.SELECT_REASON) {
                        IconButton(onClick = { closeScreen() }) {
                            Icon(
                                imageVector = if (onClose != null) Icons.Default.Close else Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(Res.string.report_close_cd),
                                tint = customColors.textPrimary
                            )
                        }
                    }
                },
                title = {
                    Text(
                        text = stringResource(Res.string.report_title),
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = customColors.textPrimary,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
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
                .padding(innerPadding)
                .background(customColors.background),
            contentAlignment = Alignment.TopCenter
        ) {
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    val isForward = targetState.ordinal > initialState.ordinal
                    val enter = slideInHorizontally(tween(250)) { if (isForward) it / 3 else -it / 3 } + fadeIn(tween(250))
                    val exit = slideOutHorizontally(tween(200)) { if (isForward) -it / 3 else it / 3 } + fadeOut(tween(200))
                    enter togetherWith exit
                },
                modifier = Modifier
                    .responsiveContentWidth()
                    .fillMaxHeight(),
                label = "ReportStepTransition"
            ) { step ->
                when (step) {
                    ReportStep.SELECT_REASON -> {
                        ReasonSelectionStep(
                            targetType = targetType,
                            onReasonSelected = { reason ->
                                selectedReason = reason
                                currentStep = ReportStep.ADD_DETAILS
                            }
                        )
                    }

                    ReportStep.ADD_DETAILS -> {
                        ReportDetailStep(
                            reason = selectedReason ?: ReportReason.OTHER,
                            description = description,
                            isSubmitting = isSubmitting,
                            onDescriptionChange = { if (it.length <= 300) description = it },
                            onSubmitClick = {
                                selectedReason?.let { reason ->
                                    onSubmitReport(reason, description.trim())
                                }
                            }
                        )
                    }

                    ReportStep.SUBMITTED -> {
                        ReportSuccessStep(
                            targetUsername = targetUsername,
                            showBlockOption = targetType == ReportTargetType.USER && onBlockUserClick != null,
                            onBlockClick = {
                                onBlockUserClick?.invoke()
                                closeScreen()
                            },
                            onDoneClick = { closeScreen() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReasonSelectionStep(
    targetType: ReportTargetType,
    onReasonSelected: (ReportReason) -> Unit
) {
    val customColors = LocalCustomColors.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = stringResource(targetType.toQuestionLabel()),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = customColors.textPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(Res.string.report_privacy_note),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = customColors.textSecondary,
                        lineHeight = 18.sp
                    )
                )
            }
            HorizontalDivider(color = customColors.borderLight, thickness = 0.5.dp)
        }

        items(ReportReason.entries) { reason ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onReasonSelected(reason) }
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(reason.toDisplayLabel()),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = customColors.textPrimary,
                        fontWeight = FontWeight.Normal
                    )
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = customColors.textSecondary
                )
            }
            HorizontalDivider(
                color = customColors.borderLight.copy(alpha = 0.6f),
                thickness = 0.5.dp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
    }
}

@Composable
private fun ReportDetailStep(
    reason: ReportReason,
    description: String,
    isSubmitting: Boolean,
    onDescriptionChange: (String) -> Unit,
    onSubmitClick: () -> Unit
) {
    val customColors = LocalCustomColors.current
    val maxCharCount = 300

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Surface(
            color = customColors.surfaceVariant,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = stringResource(Res.string.report_selected_reason_label),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = customColors.textSecondary
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(reason.toDisplayLabel()),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = customColors.textPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            TastyTextField(
                value = description,
                onValueChange = onDescriptionChange,
                label = stringResource(Res.string.report_details_label),
                placeholder = stringResource(Res.string.report_details_placeholder),
                singleLine = false,
                minLines = 4,
                maxLines = 6
            )

            Text(
                text = "${description.length} / $maxCharCount",
                style = MaterialTheme.typography.labelSmall,
                color = if (description.length >= maxCharCount) customColors.error else customColors.textTertiary,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 4.dp, end = 4.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        TastyButton(
            text = stringResource(Res.string.report_submit_button),
            onClick = onSubmitClick,
            isPrimary = true,
            isLoading = isSubmitting,
            backcolor = customColors.gourmetOrange,
            textcolor = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
    }
}

@Composable
private fun ReportSuccessStep(
    targetUsername: String?,
    showBlockOption: Boolean,
    onBlockClick: () -> Unit,
    onDoneClick: () -> Unit
) {
    val customColors = LocalCustomColors.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(customColors.gourmetOrange.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = customColors.gourmetOrange,
                modifier = Modifier.size(42.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(Res.string.report_success_title),
            style = MaterialTheme.typography.titleLarge.copy(
                color = customColors.textPrimary,
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(Res.string.report_success_subtitle),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = customColors.textSecondary,
                lineHeight = 20.sp
            ),
            textAlign = TextAlign.Center
        )

        if (showBlockOption) {
            Spacer(modifier = Modifier.height(28.dp))
            Surface(
                onClick = onBlockClick,
                color = customColors.surfaceVariant,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Block,
                        contentDescription = null,
                        tint = customColors.red
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (!targetUsername.isNullOrBlank()) {
                                stringResource(Res.string.report_block_user_named, targetUsername)
                            } else {
                                stringResource(Res.string.report_block_user_generic)
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = customColors.red,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Text(
                            text = stringResource(Res.string.report_block_user_desc),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = customColors.textSecondary
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        TastyButton(
            text = stringResource(Res.string.report_done_button),
            onClick = onDoneClick,
            isPrimary = true,
            backcolor = customColors.navy,
            textcolor = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
    }
}
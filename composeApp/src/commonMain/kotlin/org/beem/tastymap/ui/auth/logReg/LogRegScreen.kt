package org.beem.tastymap.ui.auth.logReg

import TastyButton
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.beem.tastymap.core.util.ToastManager
import org.beem.tastymap.ui.animations.TastyAnimations
import org.beem.tastymap.ui.auth.common.AuthEffect
import org.beem.tastymap.ui.auth.forgotPassword.ForgotScreen
import org.beem.tastymap.ui.auth.verification.email.EmailVerificationScreen
import org.beem.tastymap.ui.auth.verification.loginPending.PendingScreen
import org.beem.tastymap.ui.components.AuthFooter
import org.beem.tastymap.ui.components.PasswordStrengthIndicator
import org.beem.tastymap.ui.components.TastyTextField
import org.beem.tastymap.ui.profile.health.OnBoardingScreen
import org.beem.tastymap.ui.profile.myprofile.MyProfileScreen
import org.beem.tastymap.ui.theme.CustomColors
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.auth_app_subtitle
import tastymap.composeapp.generated.resources.auth_resend_prompt
import tastymap.composeapp.generated.resources.auth_resend_timer
import tastymap.composeapp.generated.resources.auth_tab_login
import tastymap.composeapp.generated.resources.auth_tab_register
import tastymap.composeapp.generated.resources.login_btn_forgot_password
import tastymap.composeapp.generated.resources.login_btn_submit
import tastymap.composeapp.generated.resources.login_email_unverified
import tastymap.composeapp.generated.resources.login_field_password
import tastymap.composeapp.generated.resources.login_field_username
import tastymap.composeapp.generated.resources.register_btn_back
import tastymap.composeapp.generated.resources.register_btn_next
import tastymap.composeapp.generated.resources.register_btn_submit
import tastymap.composeapp.generated.resources.register_field_email
import tastymap.composeapp.generated.resources.register_field_name
import tastymap.composeapp.generated.resources.register_field_password
import tastymap.composeapp.generated.resources.register_field_surname
import tastymap.composeapp.generated.resources.register_field_username
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class LogRegScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<LogRegScreenModel>()
        val regState by screenModel.registerState.collectAsState()
        val logState by screenModel.loginState.collectAsState()
        val colors = LocalCustomColors.current

        AuthEffectHandler(screenModel, navigator)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
        )

        MapHeaderSection(
            modifier = Modifier.fillMaxWidth().height(280.dp),
            bgColor = colors.wave,
            lineColor = colors.lineAlpha,
            iconColor = colors.navy
        )
        var isLoginTab by remember { mutableStateOf(true) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(top = 220.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeaderTitles(colors.textPrimary)

            Spacer(modifier = Modifier.height(5.dp))

            Card(
                modifier = Modifier
                    .widthIn(max = 400.dp)
                    .fillMaxWidth()
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(28.dp),
                        spotColor = colors.navy.copy(alpha = 0.08f)
                    ),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface),
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AuthTabBar(
                        isLoginTab = isLoginTab,
                        activeTabColor = colors.navy,
                        activeTextColor = colors.surface,
                        tabContainerColor = colors.surfaceVariant,
                        inactiveTextColor = colors.textSecondary,
                        onLoginClick = {
                            isLoginTab = true
                            screenModel.clearRegisterForm()
                            screenModel.previousRegisterStep()
                        },
                        onRegisterClick = {
                            isLoginTab = false
                            screenModel.clearLoginForm()
                        }
                    )

                    AnimatedContent(
                        targetState = isLoginTab,
                        transitionSpec = {
                            if (targetState > initialState) {
                                TastyAnimations.slideInBackward()
                            } else {
                                TastyAnimations.slideInForward()
                            }.using(SizeTransform(clip = false))
                        },
                        label = "FormAnim"
                    ) { targetIsLogin ->
                        key(targetIsLogin) {
                            if (targetIsLogin) {
                                LoginForm(colors, screenModel, logState, navigator)
                            } else {
                                RegisterForm(colors, screenModel, regState)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(25.dp))

            AuthFooter(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            )
        }
        val currentLoading = if (isLoginTab) logState.isLoading else regState.isLoading
        FullScreenLoading(isLoading = currentLoading, scrimColor = colors.textPrimary.copy(alpha = 0.4f))
    }
}

@Composable
fun FullScreenLoading(isLoading: Boolean, scrimColor: Color) {
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {}
                .background(scrimColor)
                .zIndex(10f),
            contentAlignment = Alignment.Center
        ) {}
    }
}

@Composable
fun AuthTabBar(
    isLoginTab: Boolean,
    activeTabColor: Color,
    activeTextColor: Color,
    tabContainerColor: Color,
    inactiveTextColor: Color,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(tabContainerColor, RoundedCornerShape(24.dp))
            .padding(4.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            TabButton(stringResource(Res.string.auth_tab_login), isLoginTab, Modifier.weight(1f), activeTabColor, activeTextColor, inactiveTextColor, onLoginClick)
            TabButton(stringResource(Res.string.auth_tab_register), !isLoginTab, Modifier.weight(1f), activeTabColor, activeTextColor, inactiveTextColor, onRegisterClick)
        }
    }
}

@Composable
fun HeaderTitles(textColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Tasty Map",
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Black, color = textColor
            )
        )
        Text(
            text = stringResource(Res.string.auth_app_subtitle) ,
            style = MaterialTheme.typography.titleMedium.copy(
                color = textColor.copy(alpha = 0.6f)
            )
        )
    }
}

@Composable
fun AuthEffectHandler(
    screenModel: LogRegScreenModel,
    navigator: Navigator
) {
    LaunchedEffect(Unit) {
        screenModel.clearRegisterForm()
        screenModel.clearLoginForm()
    }
    LaunchedEffect(Unit) {
        screenModel.effect.collect { effect ->
            when (effect) {
                is AuthEffect.NavigateToPending -> {
                    navigator.replaceAll(PendingScreen(effect.deviceId))
                }
                is AuthEffect.NavigateToValidate -> {
                    navigator.push(EmailVerificationScreen(effect.email, effect.deviceId, effect.userId))
                }
                is AuthEffect.NavigateToWelcome -> {
                    navigator.replaceAll(OnBoardingScreen())
                }
                is AuthEffect.NavigateToHome -> {
                    navigator.replaceAll(MyProfileScreen())
                }
                else -> Unit
            }
        }
    }

    LaunchedEffect(screenModel.uiMessage) {
        screenModel.uiMessage.collect { message ->
            val text = when (message) {
                is LogRegScreenModel.UiMessage.Dynamic -> message.message
                is LogRegScreenModel.UiMessage.Resource -> getString(message.res)
            }
            ToastManager.show(text)
        }
    }
}

@Composable
fun MapHeaderSection(
    modifier: Modifier,
    bgColor: Color,
    lineColor: Color,
    iconColor: Color
) {
    Box(modifier = modifier.graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)) {
        val infiniteTransition = rememberInfiniteTransition(label = "wave")
        val waveOffset by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = (2 * PI).toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(4000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "offset"
        )

        BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
            val w = constraints.maxWidth.toFloat()
            val h = constraints.maxHeight.toFloat()
            Canvas(modifier = Modifier.fillMaxSize()) {
                val amplitude = 45f

                val wave1 = sin(waveOffset.toDouble()).toFloat() * amplitude
                val wave2 = cos(waveOffset.toDouble()).toFloat() * amplitude

                val bgPath = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(0f, (h * 0.75f) + wave1)

                    cubicTo(
                        w * 0.3f, (h * 0.95f) + wave2,
                        w * 0.7f, (h * 0.55f) - wave1,
                        w, (h * 0.80f) + wave2
                    )

                    lineTo(w, 0f)
                    close()
                }

                drawPath(
                    path = bgPath,
                    color = bgColor
                )

                clipPath(bgPath) {
                    val mainRoad = Stroke(width = 4.dp.toPx())
                    val secondaryRoad = Stroke(width = 1.5.dp.toPx())

                    fun road(
                        start: Offset,
                        cp1: Offset,
                        cp2: Offset,
                        end: Offset,
                        style: Stroke,
                        alpha: Float = 1f
                    ) {
                        val path = Path().apply {
                            moveTo(start.x, start.y)
                            cubicTo(cp1.x, cp1.y, cp2.x, cp2.y, end.x, end.y)
                        }
                        drawPath(path, lineColor.copy(alpha = alpha), style = style)
                    }

                    road(
                        Offset(w * 0.3f, 0f),
                        Offset(w * 0.2f, h * 0.4f),
                        Offset(w * 0.5f, h * 0.7f),
                        Offset(w * 0.4f, h),
                        mainRoad, 0.4f
                    )

                    road(
                        Offset(0f, h * 0.4f),
                        Offset(w * 0.3f, h * 0.35f),
                        Offset(w * 0.7f, h * 0.6f),
                        Offset(w, h * 0.5f),
                        mainRoad, 0.4f
                    )
                    road(
                        Offset(0f, h * 0.8f),
                        Offset(w * 0.7f, h * 0.6f),
                        Offset(w * 0.3f, h * 0.35f),
                        Offset(w, h * 0.7f),
                        mainRoad, 0.1f
                    )

                    road(
                        Offset(w * 0.4f, h * 0.45f),
                        Offset(w * 0.6f, h * 0.2f),
                        Offset(w * 0.8f, h * 0.3f),
                        Offset(w, h * 0.1f),
                        secondaryRoad, 0.3f
                    )

                    road(
                        Offset(w * 0.25f, h * 0.2f),
                        Offset(w * 0.1f, h * 0.5f),
                        Offset(w * 0.3f, h * 0.8f),
                        Offset(0f, h * 0.9f),
                        secondaryRoad, 0.2f
                    )

                    road(
                        Offset(w * 0.7f, 0f),
                        Offset(w * 0.9f, h * 0.4f),
                        Offset(w * 0.6f, h * 0.6f),
                        Offset(w * 0.8f, h),
                        secondaryRoad, 0.2f
                    )
                }
            }

            IconMarker(Icons.Default.Restaurant, 0.18f, 0.10f, iconColor)
            IconMarker(Icons.Default.LocationOn, 0.52f, 0.45f, iconColor)
            IconMarker(Icons.Default.Restaurant, 0.85f, 0.25f, iconColor)
            IconMarker(Icons.Default.LocationOn, 0.15f, 0.55f, iconColor)
            IconMarker(Icons.Default.Restaurant, 0.40f, 0.20f, iconColor)
            IconMarker(Icons.Default.LocationOn, 0.60f, 0.18f, iconColor)
        }
    }
}

@Composable
fun BoxScope.IconMarker(
    icon: ImageVector,
    xPercent: Float,
    yPercent: Float,
    color: Color
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val offsetX = maxWidth * xPercent
        val offsetY = maxHeight * yPercent

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier
                .offset(x = offsetX, y = offsetY)
                .size(24.dp)
        )
    }
}

@Composable
fun TabButton(
    text: String,
    isSelected: Boolean,
    modifier: Modifier,
    activeColor: Color,
    activeTextColor: Color,
    inactiveTextColor: Color,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(if (isSelected) activeColor else Color.Transparent)
    val textColor by animateColorAsState(if (isSelected) activeTextColor else inactiveTextColor)

    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(backgroundColor, RoundedCornerShape(25.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = textColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
fun LoginForm(
    colors: CustomColors,
    vm: LogRegScreenModel,
    state: LoginUiState,
    navigator: Navigator
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        TastyTextField(
            value = state.loginUsername,
            onValueChange = { vm.onLoginEvent(LoginEvent.UsernameChanged(it)) },
            label = stringResource(Res.string.login_field_username),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null
                )
            },
            isPassword = false,
            error = state.loginUsernameError?.let { stringResource(it) }
        )

        TastyTextField(
            value = state.loginPassword,
            onValueChange = { vm.onLoginEvent(LoginEvent.PasswordChanged(it)) },
            label = stringResource(Res.string.login_field_password),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Password,
                    contentDescription = null
                )
            },
            isPassword = true,
            error = state.logPasswordError?.let { stringResource(it) }
        )

        Spacer(modifier = Modifier.height(5.dp))

        TastyButton(
            text = stringResource(Res.string.login_btn_submit),
            modifier = Modifier.fillMaxWidth(),
            isLoading = state.isLoading,
            onClick = { vm.login() },
            isPrimary = true,
            backcolor = colors.navy,
            textcolor = colors.surface,
            strokecolor = Color.Transparent
        )
        Spacer(modifier = Modifier.height(3.dp))

        ResendSection(
            onResendClick = { vm.resendVerificationEmail() },
            colors = colors,
            screenModel = vm,
            state = state
        )

        TastyButton(
            text = stringResource(Res.string.login_btn_forgot_password),
            onClick = { navigator.push(ForgotScreen()) },
            modifier = Modifier.fillMaxWidth(),
            isPrimary = false,
            backcolor = Color.Transparent,
            textcolor = colors.textPrimary,
            strokecolor = Color.Transparent
        )
    }
}

@Composable
fun ResendSection(
    onResendClick: () -> Unit,
    colors: CustomColors,
    screenModel: LogRegScreenModel,
    state: LoginUiState
) {
    val timeLeft by screenModel.timeLeft.collectAsState()
    val isButtonEnabled = timeLeft == 0

    if (state.isEmailNotVerified) {
        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.error.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp))
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(Res.string.login_email_unverified, state.unverifiedEmail),
                color = colors.error,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                enabled = isButtonEnabled,
                onClick = {
                    if (isButtonEnabled) {
                        screenModel.startTimer()
                        onResendClick()
                    }
                }
            ) {
                Text(
                    text = if (isButtonEnabled) {
                        stringResource(Res.string.auth_resend_prompt)
                    } else {
                        stringResource(Res.string.auth_resend_timer, timeLeft)
                    },
                    color = if (isButtonEnabled) colors.navy else colors.textSecondary,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }
    }
}

@Composable
fun RegisterForm(color: CustomColors, vm: LogRegScreenModel, state: RegisterUiState) {
    AnimatedContent(
        targetState = state.step,
        transitionSpec = {
            if (targetState > initialState) {
                (slideInHorizontally { it } + fadeIn()) togetherWith
                        (slideOutHorizontally { -it } + fadeOut())
            } else {
                (slideInHorizontally { -it } + fadeIn()) togetherWith
                        (slideOutHorizontally { it } + fadeOut())
            }.using(SizeTransform(clip = false))
        },
        label = "RegisterSteps"
    ) { currentStep ->
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Spacer(modifier = Modifier.height(1.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(2) { index ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(width = 32.dp, height = 4.dp)
                            .background(
                                if (currentStep > index) color.navy else color.borderLight,
                                RoundedCornerShape(2.dp)
                            )
                    )
                }
            }

            if (currentStep == 1) {
                TastyTextField(
                    value = state.regName,
                    onValueChange = { vm.onRegisterEvent(RegisterEvent.NameChanged(it)) },
                    label = stringResource(Res.string.register_field_name),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null
                        )
                    },
                    error = state.regNameError?.let { stringResource(it) }
                )

                TastyTextField(
                    value = state.regSurname,
                    onValueChange = { vm.onRegisterEvent(RegisterEvent.SurnameChanged(it)) },
                    label = stringResource(Res.string.register_field_surname),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null
                        )
                    },
                    error = state.regSurnameError?.let { stringResource(it) }
                )

                TastyTextField(
                    value = state.regUsername,
                    onValueChange = { vm.onRegisterEvent(RegisterEvent.UsernameChanged(it)) },
                    label = stringResource(Res.string.register_field_username),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null
                        )
                    },
                    error = state.regUsernameError?.let { stringResource(it) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                TastyButton(
                    text = stringResource(Res.string.register_btn_next),
                    onClick = { vm.nextRegisterStep() },
                    modifier = Modifier.fillMaxWidth(),
                    isPrimary = true,
                    backcolor = color.navy,
                    textcolor = color.surface,
                    strokecolor = Color.Transparent
                )
            } else {
                TastyTextField(
                    value = state.regEmail,
                    onValueChange = { vm.onRegisterEvent(RegisterEvent.EmailChanged(it)) },
                    label = stringResource(Res.string.register_field_email),
                    leadingIcon = { Icon(Icons.Default.Email, null) },
                    error = state.regEmailError?.let { stringResource(it) }
                )

                TastyTextField(
                    value = state.regPassword,
                    onValueChange = { vm.onRegisterEvent(RegisterEvent.PasswordChanged(it)) },
                    label = stringResource(Res.string.register_field_password),
                    leadingIcon = { Icon(Icons.Default.Password, null) },
                    isPassword = true,
                    error = state.regPasswordError?.let { stringResource(it) }
                )

                AnimatedVisibility(
                    visible = state.regPassword.isNotEmpty(),
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(4.dp))
                        PasswordStrengthIndicator(state.passwordStrength, color)
                    }
                }

                Spacer(modifier = Modifier.height(5.dp))

                TastyButton(
                    text = stringResource(Res.string.register_btn_submit),
                    isLoading = state.isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { vm.register() },
                    isPrimary = true,
                    backcolor = color.navy,
                    textcolor = color.surface,
                    strokecolor = Color.Transparent
                )
                Spacer(modifier = Modifier.height(3.dp))
                TastyButton(
                    text = stringResource(Res.string.register_btn_back),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { vm.previousRegisterStep() },
                    isPrimary = false,
                    backcolor = Color.Transparent,
                    textcolor = color.textPrimary,
                    strokecolor = color.borderLight
                )
            }
        }
    }
}
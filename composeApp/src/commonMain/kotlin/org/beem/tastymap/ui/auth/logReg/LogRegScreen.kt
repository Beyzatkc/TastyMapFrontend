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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import org.beem.tastymap.ui.components.TastyTextField
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.Navigator
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import org.koin.compose.koinInject
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.zIndex
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.beem.tastymap.ui.animations.TastyAnimations
import org.beem.tastymap.ui.auth.common.AuthEffect
import org.beem.tastymap.ui.auth.verification.EmailVerificationScreen
import org.beem.tastymap.ui.auth.common.LoginUiState
import org.beem.tastymap.ui.auth.common.PasswordStrength
import org.beem.tastymap.ui.auth.common.RegisterUiState
import org.beem.tastymap.ui.auth.verification.PendingScreen
import org.beem.tastymap.ui.post.PostScreen
import org.beem.tastymap.ui.theme.CustomColors
import org.beem.tastymap.ui.theme.LightCustomColors
import org.beem.tastymap.ui.theme.LocalCustomColors

class LogRegScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val koinInstance = koinInject<LogRegScreenModel>()
        val screenModel = rememberScreenModel { koinInstance }
        val regState by screenModel.registerState.collectAsState()
        val logState by screenModel.loginState.collectAsState()
        val colors = LocalCustomColors.current

        AuthEffectHandler(screenModel,navigator)


        Box(modifier = Modifier.fillMaxSize()
            .background(colors.backgroundBlue)
            .imePadding(),)
        {

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
                    .verticalScroll(rememberScrollState())
                    .padding(top = 260.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HeaderTitles(colors.navy)

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
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AuthTabBar(isLoginTab, colors.navy,colors.gray,
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
                                    LoginForm(colors.navy, screenModel,logState)
                                } else {
                                    RegisterForm(colors, screenModel,regState)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))

                FooterLinks(colors.navy)

                Spacer(modifier = Modifier.height(50.dp))


                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        navigator.push(PostScreen())
                    },
                    modifier = Modifier
                        .widthIn(max = 300.dp)
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.navy)
                ) {
                    Text("Keşfetmeye Başla", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }


            }
            val currentLoading = if (isLoginTab) logState.isLoading else regState.isLoading
            FullScreenLoading(isLoading = currentLoading)
        }
    }
}

@Composable
fun FullScreenLoading(isLoading: Boolean){
    if(isLoading){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit){}
                .background(Color.Black.copy(alpha = 0.4f))
                .zIndex(10f),
            contentAlignment = Alignment.Center
        ){
        }
    }

}
@Composable
fun AuthTabBar(
    isLoginTab: Boolean,
    navyIcons: Color,
    bck: Color,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(bck, RoundedCornerShape(24.dp))
            .padding(4.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            TabButton("Giriş", isLoginTab, Modifier.weight(1f), navyIcons, onLoginClick)
            TabButton("Kayıt", !isLoginTab, Modifier.weight(1f), navyIcons, onRegisterClick)
        }
    }
}
@Composable
fun HeaderTitles(navyIcons: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Tasty Map",
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Black, color = navyIcons
            )
        )
        Text(
            text = "Lezzeti Keşfet",
            style = MaterialTheme.typography.titleMedium.copy(
                color = navyIcons.copy(alpha = 0.6f)
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
        screenModel.effect.collect { effect ->
            when (effect) {
                is AuthEffect.NavigateToHome -> {}
                is AuthEffect.NavigateToLogin -> { /* ... */ }
                is AuthEffect.NavigateToPending -> {
                    navigator.replaceAll(PendingScreen(effect.approvedRefreshRequestDTO))
                }
                is AuthEffect.NavigateToValidate -> {
                    navigator.push(EmailVerificationScreen(effect.email))
                }
            }
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
                //drawPath(bgPath, bgColor)
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

                    val junctions = listOf(
                        Offset(w * 0.28f, h * 0.38f),
                        Offset(w * 0.55f, h * 0.52f),
                        Offset(w * 0.78f, h * 0.38f)
                    )

                    junctions.forEach {
                        drawCircle(
                            color = lineColor.copy(alpha = 0.5f),
                            radius = 5.dp.toPx(),
                            center = it
                        )
                    }
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
fun TabButton(text: String, isSelected: Boolean, modifier: Modifier, color: Color, onClick: () -> Unit) {
    val backgroundColor by animateColorAsState(if (isSelected) color else Color.Transparent)
    val textColor by animateColorAsState(if (isSelected) Color.White else Color.Gray)

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
fun FooterLinks(navyIcons: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val linkStyle = MaterialTheme.typography.bodySmall.copy(
            color = navyIcons.copy(alpha = 0.5f),
            fontWeight = FontWeight.Medium
        )

        Text(text = "Gizlilik", modifier = Modifier.padding(horizontal = 8.dp), style = linkStyle)
        Text(text = "•", color = navyIcons.copy(alpha = 0.3f))
        Text(text = "Kullanım Şartları", modifier = Modifier.padding(horizontal = 8.dp), style = linkStyle)
        Text(text = "•", color = navyIcons.copy(alpha = 0.3f))
        Text(text = "Destek", modifier = Modifier.padding(horizontal = 8.dp), style = linkStyle)
    }
}
@Composable fun LoginForm(color: Color, vm: LogRegScreenModel, state: LoginUiState,) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        TastyTextField(
            value = state.loginUsername,
            onValueChange = {
                vm.onLoginEvent(LoginEvent.UsernameChanged(it))
                            },
            label = "Kullanıcı adı",
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null
                )
            },
            isPassword = false,
            error = state.loginUsernameError
        )

        TastyTextField(
            value = state.loginPassword,
            onValueChange = {
                vm.onLoginEvent(LoginEvent.PasswordChanged(it)) },
            label = "Şifre",
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Password,
                    contentDescription = null
                )
            },
            isPassword = true,
            error = state.logPasswordError
        )
        Spacer(modifier = Modifier.height(5.dp))
        TastyButton(
            text = "Giriş Yap",
            isLoading = state.isLoading,
            onClick = {
                vm.login()
            },
            isPrimary = true,
            backcolor = color,
            textcolor = Color.White,
            strokecolor = color
        )
        TastyButton(
            text = "Şifreni mi unuttun",
            onClick = { /* Login Logic */ },
            isPrimary = false,
            backcolor = Color.White,
            textcolor = color,
            strokecolor = Color.White
        )
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
                                if (currentStep > index) color.navy else Color.LightGray,
                                RoundedCornerShape(2.dp)
                            )
                    )
                }
            }

            if (currentStep == 1) {
                TastyTextField(
                    value = state.regName,
                    onValueChange = {
                        vm.onRegisterEvent(RegisterEvent.NameChanged(it)) },
                    label = "Ad",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null
                        )
                    },
                    error = state.regnameError
                )
                TastyTextField(
                    value = state.regSurname,
                    onValueChange = {
                        vm.onRegisterEvent(RegisterEvent.SurnameChanged(it))
                                    },
                    label = "Soyad",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null
                        )
                    },
                    error=state.regSurnameError
                )
                TastyTextField(
                    value = state.regUsername,
                    onValueChange = {
                        vm.onRegisterEvent(RegisterEvent.UsernameChanged(it))
                                    },
                    label = "Kullanıcı Adı",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null
                        )
                    },
                    error=state.regusernameError
                )

                Spacer(modifier = Modifier.height(8.dp))
                TastyButton(
                    text = "Devam Et",
                    onClick = {
                        vm.nextRegisterStep() },
                    isPrimary = true,
                    backcolor = color.navy,
                    textcolor = Color.White,
                    strokecolor = color.navy
                )
            } else {
                TastyTextField(
                    value = state.regEmail,
                    onValueChange = {
                        vm.onRegisterEvent(RegisterEvent.EmailChanged(it)) },
                    label = "Email",
                    leadingIcon = { Icon(Icons.Default.Email, null) },
                    error=state.regEmailError
                )
                TastyTextField(
                    value = state.regPassword,
                    onValueChange = {
                        vm.onRegisterEvent(RegisterEvent.PasswordChanged(it)) },
                    label = "Şifre",
                    leadingIcon = { Icon(Icons.Default.Password, null) },
                    isPassword = true,
                    error=state.regPasswordError
                )
                AnimatedVisibility(
                    visible = state.regPassword.isNotEmpty(),
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(4.dp))
                        PasswordStrengthIndicator(state.passwordStrength,color)
                    }
                }
                Spacer(modifier = Modifier.height(5.dp))

                TastyButton(
                    text = "Kayıt Ol",
                    isLoading = state.isLoading,
                    onClick = {
                        vm.register()
                    },
                    isPrimary = true,
                    backcolor = color.navy,
                    textcolor = Color.White,
                    strokecolor = color.navy
                )

                TastyButton(
                    text = "Geri Dön",
                    onClick = { vm.previousRegisterStep() },
                    isPrimary = false,
                    backcolor = Color.White,
                    textcolor = color.navy,
                    strokecolor = Color.Transparent
                )
            }
        }
    }
}@Composable
fun PasswordStrengthIndicator(
    passwordStrength: PasswordStrength,
    colors: CustomColors
) {
    val checks = listOf(
        "8+ karakter" to passwordStrength.hasMinLength,
        "Büyük harf" to passwordStrength.hasUppercase,
        "Rakam" to passwordStrength.hasDigit,
        "Özel karakter" to passwordStrength.hasSpecialChar
    )

    val score = checks.count { it.second }

    val (strengthText, color) = when (score) {
        0, 1 -> "Zayıf" to colors.red
        2, 3 -> "Orta" to colors.yellow
        else -> "Güçlü" to colors.green
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Şifre",
                style = MaterialTheme.typography.labelSmall
            )

            Text(
                text = strengthText,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(3.dp))

        LinearProgressIndicator(
            progress = { score / 4f },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(50)),
            color = color
        )

        Spacer(modifier = Modifier.height(4.dp))

        checks.forEach { (text, passed) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 1.dp)
            ) {

                Icon(
                    imageVector = if (passed)
                        Icons.Default.CheckCircle
                    else
                        Icons.Default.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (passed) colors.green else Color.Gray,
                    modifier = Modifier.size(14.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = text,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (passed)
                        MaterialTheme.colorScheme.onSurface
                    else
                        Color.Gray
                )
            }
        }
    }
}
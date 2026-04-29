package org.beem.tastymap.ui.auth
import TastyButton
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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

class LoginScreen : Screen {
    @Composable
    override fun Content() {
        var isLoginTab by remember { mutableStateOf(true) }
        val koinInstance = koinInject<AuthScreenModel>()
        val screenModel = rememberScreenModel { koinInstance }

        val navyIcons = Color(0xFF001970)
        val mapBackgroundBlue = Color(0xFFE3F2FD)
        val darkGrayLines = Color(0xFF444444).copy(alpha = 0.2f)
        val backBackgroundBlue = Color(0xFFF2F2F2)

        Box(modifier = Modifier.fillMaxSize().background(backBackgroundBlue)) {

            MapHeaderSection(
                modifier = Modifier.fillMaxWidth().height(280.dp),
                bgColor = mapBackgroundBlue,
                lineColor = darkGrayLines,
                iconColor = navyIcons
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(top = 260.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.widthIn(max = 400.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Tasty Map",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Black,
                            color = navyIcons
                        )
                    )
                    Text(
                        text = "Lezzeti Keşfet",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = navyIcons.copy(alpha = 0.6f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(5.dp))


                Card(
                    modifier = Modifier
                        .widthIn(max = 400.dp)
                        .fillMaxWidth()
                        .shadow(
                            elevation = 20.dp,
                            shape = RoundedCornerShape(28.dp),
                            spotColor = navyIcons.copy(alpha = 0.25f)
                        )
                        .animateContentSize(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp), // İç padding
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .background(Color(0xFFF5F5F5), RoundedCornerShape(24.dp))
                                .padding(4.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxSize()) {
                                TabButton("Giriş", isLoginTab, Modifier.weight(1f), navyIcons) { isLoginTab = true }
                                TabButton("Kayıt", !isLoginTab, Modifier.weight(1f), navyIcons) { isLoginTab = false }
                            }
                        }

                        AnimatedContent(
                            targetState = isLoginTab,
                            transitionSpec = {
                                fadeIn(tween(300)) togetherWith fadeOut(tween(300))
                            },
                            label = "FormAnim"
                        ) { targetIsLogin ->
                            if (targetIsLogin) {
                                LoginForm(navyIcons, screenModel)
                                screenModel.clearRegisterForm()
                            } else{
                                RegisterForm(navyIcons,screenModel)
                                screenModel.clearLoginForm()
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))

                FooterLinks(navyIcons)

                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}
/*
@Composable
fun AuthEffectHandler(
    screenModel: AuthScreenModel,
    navigator: Navigator
) {
    LaunchedEffect(Unit) {
        screenModel.effect.collect { effect ->
            when (effect) {
                is AuthEffect.NavigateToHome -> navigator.push(HomeScreen())
                is AuthEffect.NavigateToLogin -> { /* ... */ }
                is AuthEffect.NavigateToPending -> { /* ... */ }
                is AuthEffect.ShowMessage ->
            }
        }
    }
}

 */
@Composable
fun MapHeaderSection(
    modifier: Modifier,
    bgColor: Color,
    lineColor: Color,
    iconColor: Color
) {
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
            drawPath(bgPath, bgColor)

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
@Composable fun LoginForm(color: Color,vm: AuthScreenModel) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        TastyTextField(
            value = vm.loginUsername,
            onValueChange = {
                vm.loginUsername = it
                vm.loginUsernameError = null
                            },
            label = "Kullanıcı adı",
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null
                )
            },
            isPassword = false,
            error = vm.loginUsernameError
        )

        TastyTextField(
            value = vm.loginPassword,
            onValueChange = {
                vm.loginPassword = it
                vm.logPasswordError = null
                            },
            label = "Şifre",
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Password,
                    contentDescription = null
                )
            },
            isPassword = true,
            error = vm.logPasswordError
        )
        Spacer(modifier = Modifier.height(5.dp))
        TastyButton(
            text = "Giriş Yap",
            isLoading = vm.isLoading,
            onClick = {
                if (vm.validateLogin()) {

                }
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
fun RegisterForm(color: Color,vm: AuthScreenModel) {
    var step by remember { mutableIntStateOf(1) }

    AnimatedContent(
        targetState = step,
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
                                if (currentStep > index) color else Color.LightGray,
                                RoundedCornerShape(2.dp)
                            )
                    )
                }
            }

            if (currentStep == 1) {
                TastyTextField(
                    value = vm.regName,
                    onValueChange = {
                        vm.regName = it
                        vm.regnameError = null
                                    },
                    label = "Ad",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null
                        )
                    },
                    error = vm.regnameError
                )
                TastyTextField(
                    value = vm.regSurname,
                    onValueChange = {
                        vm.regSurname = it
                        vm.regSurnameError = null
                                    },
                    label = "Soyad",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null
                        )
                    },
                    error=vm.regSurnameError
                )
                TastyTextField(
                    value = vm.regUsername,
                    onValueChange = {
                        vm.regUsername = it
                        vm.regusernameError = null
                                    },
                    label = "Kullanıcı Adı",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null
                        )
                    },
                    error=vm.regusernameError
                )

                Spacer(modifier = Modifier.height(8.dp))
                TastyButton(
                    text = "Devam Et",
                    onClick = {
                        if (vm.validateRegisterStep1()) {
                            step = 2
                        }
                              },
                    isPrimary = true,
                    backcolor = color,
                    textcolor = Color.White,
                    strokecolor = color
                )
            } else {
                TastyTextField(
                    value = vm.regEmail,
                    onValueChange = {
                        vm.regEmail = it
                        vm.regEmailError = null
                                    },
                    label = "Email",
                    leadingIcon = { Icon(Icons.Default.Email, null) },
                    error=vm.regEmailError
                )
                TastyTextField(
                    value = vm.regPassword,
                    onValueChange = {
                        vm.regPassword = it
                        vm.regPasswordError = null
                                    },
                    label = "Şifre",
                    leadingIcon = { Icon(Icons.Default.Password, null) },
                    isPassword = true,
                    error=vm.regPasswordError
                )

                Spacer(modifier = Modifier.height(5.dp))

                TastyButton(
                    text = "Kayıt Ol",
                    isLoading = vm.isLoading,
                    onClick = {
                        vm.register()
                    },
                    isPrimary = true,
                    backcolor = color,
                    textcolor = Color.White,
                    strokecolor = color
                )

                TastyButton(
                    text = "Geri Dön",
                    onClick = { step = 1 },
                    isPrimary = false,
                    backcolor = Color.White,
                    textcolor = color,
                    strokecolor = Color.Transparent
                )
            }
        }
    }
}
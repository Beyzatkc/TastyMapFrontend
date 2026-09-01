package org.beem.tastymap.ui.profile.myprofile.editprofile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShortText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerType
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.launch
import org.beem.tastymap.core.util.ToastManager
import org.beem.tastymap.domain.model.UserProfile
import org.beem.tastymap.ui.components.TastyTextField
import org.beem.tastymap.ui.profile.myprofile.MyProfileScreenModel
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.beem.tastymap.ui.theme.TastyTheme

class EditProfileScreen : Screen {

    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<MyProfileScreenModel>()
        val uiState by screenModel.myProfileState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(uiState.successMessage) {
            if (uiState.successMessage != null) {
                ToastManager.show(uiState.successMessage.toString())
                screenModel.clearMessagesEdit()
                navigator.pop()
            }
        }

        LaunchedEffect(uiState.errorMessage) {
            uiState.errorMessage?.let { error ->
                ToastManager.show(error)
                screenModel.clearMessagesEdit()
            }
        }

        EditProfileContent(
            userProfile = uiState.profile,
            isLoading = uiState.isActionLoading,
            usernameError = uiState.usernameError,
            nameError = uiState.nameError,
            surnameError = uiState.surnameError,
            onBackClick = { navigator.pop() },
            onSaveClick = { username, name, surname, biography, platformFile ->
                screenModel.updateProfile(
                    inputUsername = username,
                    inputName = name,
                    inputSurname = surname,
                    inputBiography = biography,
                    selectedFile = platformFile
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileContent(
    userProfile: UserProfile?,
    isLoading: Boolean = false,
    usernameError: String? = null,
    nameError: String? = null,
    surnameError: String? = null,
    onBackClick: () -> Unit = {},
    onSaveClick: (username: String, name: String, surname: String, biography: String?, file: PlatformFile?) -> Unit
) {
    val customColors = LocalCustomColors.current
    val scope = rememberCoroutineScope()
    var selectedFile by remember { mutableStateOf<PlatformFile?>(null) }
    var selectedImageBytes by remember { mutableStateOf<ByteArray?>(null) }

    val launcher = rememberFilePickerLauncher(
        type = PickerType.Image
    ) { file: PlatformFile? ->
        file?.let {
            selectedFile = it
            scope.launch {
                selectedImageBytes = it.readBytes()
            }
        }
    }

    var username by rememberSaveable(userProfile?.username) { mutableStateOf(userProfile?.username.orEmpty()) }
    var name by rememberSaveable(userProfile?.name) { mutableStateOf(userProfile?.name.orEmpty()) }
    var surname by rememberSaveable(userProfile?.surname) { mutableStateOf(userProfile?.surname.orEmpty()) }
    var biography by rememberSaveable(userProfile?.biography) { mutableStateOf(userProfile?.biography.orEmpty()) }

    Scaffold(
        containerColor = customColors.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profili Düzenle",
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
                            contentDescription = "Geri",
                            tint = customColors.textPrimary
                        )
                    }
                },
                actions = {
                    TextButton(
                        enabled = !isLoading,
                        onClick = {
                            onSaveClick(username, name, surname, biography, selectedFile)
                        }
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = customColors.gourmetOrange,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Kaydet",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                ) {
                    Box(
                        modifier = Modifier.align(Alignment.BottomCenter)
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .border(3.dp, customColors.background, CircleShape),
                            color = customColors.placeHolderBack
                        ) {
                            when {
                                selectedImageBytes != null -> {
                                    AsyncImage(
                                        model = selectedImageBytes,
                                        contentDescription = "Seçilen Profil Fotoğrafı",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                !userProfile?.profilePhoto.isNullOrEmpty() -> {
                                    AsyncImage(
                                        model = userProfile?.profilePhoto,
                                        contentDescription = "Profil Fotoğrafı",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                else -> {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = customColors.placeHolderIcon,
                                        modifier = Modifier.padding(22.dp)
                                    )
                                }
                            }
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = 6.dp, y = 6.dp)
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(customColors.navy)
                                .clickable {
                                    launcher.launch()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Fotoğraf Değiştir",
                                tint = customColors.surface,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SectionHeader(
                        icon = Icons.Default.Person,
                        title = "Kişisel Bilgiler"
                    )
                    Card(
                        colors = CardDefaults.cardColors(containerColor = customColors.surface),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            TastyTextField(
                                value = username,
                                onValueChange = { username = it },
                                label = "Kullanıcı Adı",
                                error = usernameError
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                TastyTextField(
                                    value = name,
                                    onValueChange = { name = it },
                                    label = "Ad",
                                    error = nameError,
                                    modifier = Modifier.weight(1f)
                                )

                                TastyTextField(
                                    value = surname,
                                    onValueChange = { surname = it },
                                    label = "Soyad",
                                    error = surnameError,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SectionHeader(
                        icon = Icons.Default.ShortText,
                        title = "Hakkımda"
                    )

                    Card(
                        colors = CardDefaults.cardColors(containerColor = customColors.surface),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            TastyTextField(
                                value = biography,
                                onValueChange = { if (it.length <= 200) biography = it },
                                label = "Biyografi",
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                            )
                            Text(
                                text = "${biography.length}/200",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = customColors.textTertiary
                                ),
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }

            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    color = customColors.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = customColors.textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Kullanıcı adınız ve biyografiniz diğer TastyMap gurmeleri tarafından görülebilir.",
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

@Composable
private fun SectionHeader(
    icon: ImageVector,
    title: String
) {
    val customColors = LocalCustomColors.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(start = 4.dp, top = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = customColors.navy,
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

@Preview
@Composable
private fun EditProfileContentLightPreview() {
    TastyTheme(useDarkTheme = false) {
        EditProfileContent(
            userProfile = UserProfile(
                userId = 1L,
                username = "gurme_ahmet",
                surname = "Yılmaz",
                name = "Ahmet",
                profilePhoto = null,
                role = "USER",
                biography = "Yeni lezzetler keşfetmeyi seviyorum 🍕",
                postCount = 0L,
                subscriberCount = 0L,
                subscribedCount = 0L,
                blockedByMe = false,
                blockedMe = false
            ),
            onSaveClick = { _, _, _, _, _ -> }
        )
    }
}
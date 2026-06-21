
import org.gradle.declarative.dsl.schema.FqName.Empty.packageName
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties



plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.sqldelight)
    kotlin("plugin.serialization") version "2.0.21"
    id("com.google.gms.google-services")
    id("com.codingfeline.buildkonfig") version "0.15.1"
}
buildkonfig {
    packageName = "org.beem.tastymap.core.util"

    val props = Properties()
    val localPropertiesFile = project.rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        props.load(localPropertiesFile.inputStream())
    }

    defaultConfigs {
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "VAPID_KEY", props.getProperty("web.vapid.key") ?: "")
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "API_HOST", props.getProperty("API_URL") ?: "10.0.2.2:8080")
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "WS_PROTOCOL", "ws")
    }
    defaultConfigs("release") {
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "API_HOST", props.getProperty("API_URL") ?: "10.0.2.2:8080")
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "WS_PROTOCOL", "wss")
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "VAPID_KEY", props.getProperty("web.vapid.key") ?: "")
    }
}


sqldelight {
    databases {
        create("TastyDatabase") {
            packageName.set("org.beem.tastymap.database")
        }
    }
}


kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    js {
        browser()
        binaries.executable()
    }
    
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }
    @OptIn(ExperimentalWasmDsl::class)

/*
    cocoapods {
        summary = "TastyMap Shared Library"
        homepage = "https://github.com/beyza/tastymap"
        ios.deploymentTarget = "15.0"
        framework {
            baseName = "ComposeApp"
            isStatic = true
        }

        // --- Firebase Pod'larını Buraya Yazıyoruz ---
        pod("FirebaseCore")
        pod("FirebaseAuth")     // Kullanıcı girişi yapacaksan
        pod("FirebaseMessaging") // Bildirimler için
        // Mackbookum olmadııg iicn hata verıyoooor
    }

 */
    
    sourceSets {
        val webMain by creating {
            dependsOn(commonMain.get())
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-browser:0.3")
            }
        }

        wasmJsMain.get().apply {
            dependsOn(webMain)
        }
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.sqldelight.android)
            implementation(libs.firebase.gitlive.messaging)
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.firebase.messaging.android)
            implementation(libs.androidx.security.crypto)
            implementation("androidx.core:core-splashscreen:1.0.1")

        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.sqldelight.native)
            implementation(libs.firebase.gitlive.messaging)
        }

        wasmJsMain.dependencies {
            implementation(libs.ktor.client.js)
            implementation(libs.sqldelight.web)
        }
        webMain.dependencies {
            implementation(libs.kotlinx.browser)
            implementation("io.ktor:ktor-client-core:3.0.0")
            implementation("io.ktor:ktor-client-js:3.0.0")
        }


        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.annotations)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.auth)

            implementation(libs.voyager.navigator)
            implementation(libs.voyager.screenmodel)
            implementation(libs.voyager.koin)
            implementation(libs.voyager.transitions)


            implementation(libs.androidx.paging.common)
            implementation(libs.androidx.paging.compose)
            implementation(libs.sqldelight.coroutines)

            implementation(libs.multiplatform.settings)
            implementation(compose.materialIconsExtended)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.websockets)

        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}


android {
    namespace = "org.beem.tastymap"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.beem.tastymap"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}


dependencies {
    debugImplementation(libs.compose.uiTooling)
}



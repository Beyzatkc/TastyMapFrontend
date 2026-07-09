package org.beem.tastymap.di

import com.russhwolf.settings.Settings
import org.beem.tastymap.core.AndroidDeviceInfoProvider
import org.beem.tastymap.core.provider.DeviceInfoProvider
import org.koin.dsl.module

import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.russhwolf.settings.SharedPreferencesSettings
import org.beem.tastymap.core.AndroidPermissionManager
import org.beem.tastymap.core.local.MobileUserManager
import org.beem.tastymap.core.local.UserManager
import org.beem.tastymap.core.navigation.AuthNavigationHandler
import org.beem.tastymap.core.network.MobileSessionValidator
import org.beem.tastymap.core.network.MobileHttpClientFactory
import org.beem.tastymap.core.permission.PermissionManager
import org.beem.tastymap.core.provider.AuthValidator
import org.beem.tastymap.core.provider.HttpClientFactory


val androidModule = module {
    single<DeviceInfoProvider> { AndroidDeviceInfoProvider(get()) }
    single<HttpClientFactory> { MobileHttpClientFactory(get(), get()) }
    single<Settings> {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        val sharedPreferences = EncryptedSharedPreferences.create(
            "secure_settings",
            masterKeyAlias,
            get(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        SharedPreferencesSettings(sharedPreferences)
    }
    single<UserManager> { MobileUserManager(get()) }
    single <AuthValidator>{ MobileSessionValidator(get(),get()) }
    single<PermissionManager> { AndroidPermissionManager(get()) }
    single { AuthNavigationHandler() }
}

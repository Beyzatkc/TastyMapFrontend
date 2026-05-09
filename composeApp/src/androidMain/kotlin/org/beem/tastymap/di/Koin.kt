package org.beem.tastymap.di

import com.russhwolf.settings.Settings
import org.beem.tastymap.core.AndroidDeviceInfoProvider
import org.beem.tastymap.core.provider.DeviceInfoProvider
import org.koin.dsl.module

import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.russhwolf.settings.SharedPreferencesSettings


val androidModule = module {
    single<DeviceInfoProvider> { AndroidDeviceInfoProvider(get()) }

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
}

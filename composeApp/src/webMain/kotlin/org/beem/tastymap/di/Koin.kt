package org.beem.tastymap.di

import com.russhwolf.settings.Settings
import com.russhwolf.settings.StorageSettings
import org.beem.tastymap.core.WebDeviceInfoProvider
import org.beem.tastymap.core.provider.DeviceInfoProvider
import org.koin.dsl.module

val webModule = module {
    single<DeviceInfoProvider> { WebDeviceInfoProvider() }
    single<Settings> {
        val storage = kotlinx.browser.window.localStorage
        StorageSettings(storage)
    }
}
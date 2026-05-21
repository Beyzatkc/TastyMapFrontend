package org.beem.tastymap.di

import com.russhwolf.settings.Settings
import com.russhwolf.settings.StorageSettings
import org.beem.tastymap.core.WebDeviceInfoProvider
import org.beem.tastymap.core.WebHttpClientFactory
import org.beem.tastymap.core.provider.DeviceInfoProvider
import org.beem.tastymap.core.provider.HttpClientFactory
import org.koin.dsl.module

val webModule = module {
    single<DeviceInfoProvider> { WebDeviceInfoProvider() }
    single<HttpClientFactory> { WebHttpClientFactory(get()) }
    single<Settings> {
        val storage = kotlinx.browser.window.localStorage
        StorageSettings(storage)
    }
}
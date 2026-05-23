package org.beem.tastymap.di

import org.beem.tastymap.core.IosDeviceInfoProvider
import org.beem.tastymap.core.provider.DeviceInfoProvider
import org.koin.dsl.module

val iosModule = module {
    single<DeviceInfoProvider> { IosDeviceInfoProvider() }
    single<HttpClientFactory> { MobileHttpClientFactory(get(), get()) }
    single<Settings> {
        KeychainSettings(service = "TastyMapService")
    }
    single<UserManager> { MobileUserManager(get()) }
    single <AuthValidator>{ MobileAuthValidator(get(),get()) }
}
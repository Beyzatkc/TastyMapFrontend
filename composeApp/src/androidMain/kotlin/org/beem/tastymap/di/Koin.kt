package org.beem.tastymap.di

import org.beem.tastymap.core.AndroidDeviceInfoProvider
import org.beem.tastymap.core.provider.DeviceInfoProvider
import org.koin.dsl.module

val androidModule = module {
    single<DeviceInfoProvider> { AndroidDeviceInfoProvider(get()) }
}
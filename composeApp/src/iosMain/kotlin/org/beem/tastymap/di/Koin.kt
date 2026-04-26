package org.beem.tastymap.di

import org.beem.tastymap.core.IosDeviceInfoProvider
import org.beem.tastymap.core.provider.DeviceInfoProvider
import org.koin.dsl.module

val iosModule = module {
    single<DeviceInfoProvider> { IosDeviceInfoProvider() }
}
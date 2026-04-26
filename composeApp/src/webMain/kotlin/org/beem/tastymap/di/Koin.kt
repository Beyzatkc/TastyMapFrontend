package org.beem.tastymap.di

import org.beem.tastymap.core.WebDeviceInfoProvider
import org.beem.tastymap.core.provider.DeviceInfoProvider
import org.koin.dsl.module

val webModule = module {
    single<DeviceInfoProvider> { WebDeviceInfoProvider() }
}
package org.beem.tastymap.di

import org.beem.tastymap.core.WebDeviceInfoProvider
import org.beem.tastymap.core.provider.DeviceInfoProvider
import org.beem.tastymap.map.LocationTracker
import org.beem.tastymap.map.WebLocationTracker
import org.koin.dsl.module

val webModule = module {
    single<DeviceInfoProvider> { WebDeviceInfoProvider() }

    single<LocationTracker> { WebLocationTracker() }
}
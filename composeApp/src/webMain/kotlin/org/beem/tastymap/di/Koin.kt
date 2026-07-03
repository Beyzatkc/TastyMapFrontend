package org.beem.tastymap.di

import com.russhwolf.settings.Settings
import com.russhwolf.settings.StorageSettings
import createNoAuthClient
import io.ktor.client.HttpClient
import org.beem.tastymap.core.WebSessionValidator
import org.beem.tastymap.core.WebDeviceInfoProvider
import org.beem.tastymap.core.WebHttpClientFactory
import org.beem.tastymap.core.local.UserManager
import org.beem.tastymap.core.local.WebUserManager
import org.beem.tastymap.core.navigation.PlatformMessenger
import org.beem.tastymap.core.navigation.WebMessenger
import org.beem.tastymap.core.permission.PermissionManager
import org.beem.tastymap.core.permission.WebPermissionManager
import org.beem.tastymap.core.provider.AuthValidator
import org.beem.tastymap.core.provider.DeviceInfoProvider
import org.beem.tastymap.core.provider.HttpClientFactory
import org.koin.core.qualifier.named
import org.koin.dsl.module

val webModule = module {
    single<DeviceInfoProvider> { WebDeviceInfoProvider() }
    single<HttpClientFactory> { WebHttpClientFactory(get()) }

    single<Settings> {
        val storage = kotlinx.browser.window.localStorage
        StorageSettings(storage)
    }
    single<UserManager> { WebUserManager() }

    single(named("noAuth")) { createNoAuthClient() }


    single(named("auth")) {
        val factory = get<HttpClientFactory>()
        val noAuth = get<HttpClient>(named("noAuth"))
        factory.createAuthClient(noAuth)
    }
    single <AuthValidator>{ WebSessionValidator(authClient = get(named("auth")),get()) }

    single<PlatformMessenger> { WebMessenger() }
    single<PermissionManager> { WebPermissionManager() }
}
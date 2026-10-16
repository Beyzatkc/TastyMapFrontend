package org.beem.tastymap.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import com.russhwolf.settings.Settings
import com.russhwolf.settings.StorageSettings
import createNoAuthClient
import io.ktor.client.HttpClient
import org.beem.tastymap.core.WebDeviceInfoProvider
import org.beem.tastymap.core.WebHttpClientFactory
import org.beem.tastymap.core.WebSessionValidator
import org.beem.tastymap.core.local.UserManager
import org.beem.tastymap.core.local.WebUserManager
import org.beem.tastymap.core.navigation.VerifyNavigator
import org.beem.tastymap.core.navigation.WebVerifyNavigator
import org.beem.tastymap.core.permission.PermissionManager
import org.beem.tastymap.core.permission.WebPermissionManager
import org.beem.tastymap.core.provider.AuthValidator
import org.beem.tastymap.core.provider.DeviceInfoProvider
import org.beem.tastymap.core.provider.HttpClientFactory
import org.beem.tastymap.database.TastyDatabase
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.w3c.dom.Worker

private val sqlDelightWorker: Worker =
    js(
        """
        new Worker(
            new URL(
                "@cashapp/sqldelight-sqljs-worker/sqljs.worker.js",
                import.meta.url
            )
        )
        """
    )

val webModule = module {

    single<SqlDriver> {
        WebWorkerDriver(sqlDelightWorker)
    }


    single<TastyDatabase> {
        val driver = get<SqlDriver>()
        TastyDatabase(driver)
    }

    single<DeviceInfoProvider> {
        WebDeviceInfoProvider()
    }

    single<HttpClientFactory> {
        WebHttpClientFactory(get(), get(),get())
    }

    single<Settings> {
        val storage = kotlinx.browser.window.localStorage
        StorageSettings(storage)
    }

    single<UserManager> {
        WebUserManager()
    }

    single(named("noAuth")) {
        createNoAuthClient(get())
    }

    single(named("auth")) {
        val factory = get<HttpClientFactory>()
        val noAuth = get<HttpClient>(named("noAuth"))

        factory.createAuthClient(noAuth)
    }

    single<AuthValidator> {
        WebSessionValidator(
            authClient = get(named("auth")),
            get()
        )
    }

    single<PermissionManager> {
        WebPermissionManager()
    }

    single<VerifyNavigator> {
        WebVerifyNavigator()
    }
}
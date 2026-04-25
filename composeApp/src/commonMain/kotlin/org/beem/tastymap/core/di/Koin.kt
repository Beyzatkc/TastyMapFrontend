package org.beem.tastymap.core.di

import org.koin.dsl.module
import com.russhwolf.settings.Settings
import createHTTPClient
import org.beem.tastymap.core.local.TokenManager
import org.beem.tastymap.core.local.TokenManagerImpl
import org.beem.tastymap.data.remote.AuthDataSource
import org.beem.tastymap.data.repository.AuthRepository
import org.beem.tastymap.ui.auth.AuthScreenModel
import kotlin.text.get
import org.koin.core.module.dsl.factoryOf

val appModule = module {
    single { Settings() }

    single<TokenManager> { TokenManagerImpl(get()) }

    single { createHTTPClient(get()) }

    single { AuthDataSource(get()) }
    single { AuthRepository(get(), get()) }
    factory { AuthScreenModel(get(),get()) }
}
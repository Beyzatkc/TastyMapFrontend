package org.beem.tastymap.core.di

import org.koin.dsl.module
import createNoAuthClient
import io.ktor.client.HttpClient
import org.beem.tastymap.core.local.TokenManager
import org.beem.tastymap.core.local.TokenManagerImpl
import org.beem.tastymap.core.navigation.EmptyMessenger
import org.beem.tastymap.core.navigation.PlatformMessenger
import org.beem.tastymap.core.provider.HttpClientFactory
import org.beem.tastymap.data.remote.AuthDataSource
import org.beem.tastymap.data.remote.UserDataSource
import org.beem.tastymap.data.repository.AuthRepository
import org.beem.tastymap.data.repository.PostRepository
import org.beem.tastymap.ui.auth.AuthScreenModel
import org.beem.tastymap.ui.auth.Splash.SplashScreenModel
import org.beem.tastymap.ui.post.PostScreenModel
import org.koin.core.qualifier.named

val appModule = module {

    single<TokenManager> { TokenManagerImpl(get()) }

    single(named("noAuth")) { createNoAuthClient() }
    single(named("auth")) {
        val factory = get<HttpClientFactory>()
        val noAuth = get<HttpClient>(named("noAuth"))
        factory.createAuthClient(noAuth)
    }

    single { AuthDataSource(get(named("noAuth"))) }
    single { UserDataSource(get(named("auth"))) }

    single { AuthRepository(get(), get(),get(),get()) }
    single { PostRepository(get()) }

    factory { AuthScreenModel(get(),get()) }
    factory { PostScreenModel (get())}
    factory { SplashScreenModel(get()) }

    single<PlatformMessenger> { EmptyMessenger() }
}
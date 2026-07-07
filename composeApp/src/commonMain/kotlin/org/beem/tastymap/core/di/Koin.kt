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
import org.beem.tastymap.data.remote.AuthWebSocketClient
import org.beem.tastymap.data.remote.UserDataSource
import org.beem.tastymap.data.repository.AuthRepository
import org.beem.tastymap.data.repository.PostRepository
import org.beem.tastymap.ui.auth.forgotPassword.ForgotScreenModel
import org.beem.tastymap.ui.auth.logReg.LogRegScreenModel
import org.beem.tastymap.ui.auth.splash.SplashScreenModel
import org.beem.tastymap.ui.auth.verification.EmailScreenModel
import org.beem.tastymap.ui.auth.verification.PendingScreenModel
import org.beem.tastymap.ui.post.PostScreenModel
import org.koin.core.qualifier.named

val appModule = module {

    single<TokenManager> { TokenManagerImpl(get()) }

    single<HttpClient>(named("noAuth")) {
        createNoAuthClient()
    }

    single<HttpClient>(named("auth")) {
        val factory = get<HttpClientFactory>()
        val noAuth = get<HttpClient>(named("noAuth"))
        factory.createAuthClient(noAuth)
    }
    single { AuthDataSource(get(named("noAuth"))) }
    single { UserDataSource(get(named("auth"))) }

    single { AuthRepository(get(), get(),get(),get()) }
    single { PostRepository(get()) }
    single { AuthWebSocketClient(get(named("auth"))) }

    factory { LogRegScreenModel(get(),get(),get()) }
    factory { PendingScreenModel(get(),get(),get()) }
    factory { EmailScreenModel(get()) }
    factory { PostScreenModel (get())}
    factory { SplashScreenModel(get()) }
    factory { ForgotScreenModel(get(),get()) }

    single<PlatformMessenger> { EmptyMessenger() }
}
package org.beem.tastymap.core.di

import org.koin.dsl.module
import createNoAuthClient
import io.ktor.client.HttpClient
import org.beem.tastymap.core.local.TokenManager
import org.beem.tastymap.core.local.TokenManagerImpl
import org.beem.tastymap.core.navigation.MobileVerifyNavigator
import org.beem.tastymap.core.navigation.VerifyNavigator
import org.beem.tastymap.core.provider.HttpClientFactory
import org.beem.tastymap.data.remote.AuthDataSource
import org.beem.tastymap.data.remote.AuthWebSocketClient
import org.beem.tastymap.data.remote.HealthDataSource
import org.beem.tastymap.data.remote.UserSecurityDataSource
import org.beem.tastymap.data.repository.AuthRepository
import org.beem.tastymap.data.repository.HealthRepository
import org.beem.tastymap.data.repository.UserSecurityRepository
import org.beem.tastymap.ui.auth.forgotPassword.ForgotScreenModel
import org.beem.tastymap.ui.auth.forgotPassword.PasswordResetSessionManager
import org.beem.tastymap.ui.auth.forgotPassword.ResetScreenModel
import org.beem.tastymap.ui.auth.logReg.LogRegScreenModel
import org.beem.tastymap.ui.splash.SplashScreenModel
import org.beem.tastymap.ui.auth.verification.email.EmailScreenModel
import org.beem.tastymap.ui.auth.verification.loginPending.PendingScreenModel
import org.beem.tastymap.ui.profile.health.HealthScreenModel
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
    single { UserSecurityDataSource(get(named("noAuth"))) }
    single { HealthDataSource(get(named("auth"))) }

    single { AuthRepository(get(), get(),get(),get()) }
    single { UserSecurityRepository(get()) }
    single { UserSecurityRepository(get()) }
    single { HealthRepository(get()) }
    single { AuthWebSocketClient(get(named("auth"))) }
    single { PasswordResetSessionManager() }

    factory { LogRegScreenModel(get(),get(),get(),get()) }
    factory { PendingScreenModel(get(),get(),get(),get()) }
    factory { EmailScreenModel(get(),get()) }
    factory { SplashScreenModel(get()) }
    factory { ForgotScreenModel(get(),get(),get(),get()) }
    factory { ResetScreenModel(get()) }
    factory { HealthScreenModel(get()) }

    single<VerifyNavigator> { MobileVerifyNavigator() }
}
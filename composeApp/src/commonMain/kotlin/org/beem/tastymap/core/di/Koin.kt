package org.beem.tastymap.core.di

import org.koin.dsl.module
import createNoAuthClient
import io.ktor.client.HttpClient
import org.beem.tastymap.core.auth.AuthEventBus
import org.beem.tastymap.core.local.SettingsManager
import org.beem.tastymap.core.local.SettingsManagerImpl
import org.beem.tastymap.core.local.TokenManager
import org.beem.tastymap.core.local.TokenManagerImpl
import org.beem.tastymap.core.navigation.MobileVerifyNavigator
import org.beem.tastymap.core.navigation.VerifyNavigator
import org.beem.tastymap.core.provider.HttpClientFactory
import org.beem.tastymap.data.cache.CacheManager
import org.beem.tastymap.data.cache.HealthMemoryCache
import org.beem.tastymap.data.cache.ProfileMemoryCache
import org.beem.tastymap.data.local.ProfileLocalDataSource
import org.beem.tastymap.data.remote.AuthDataSource
import org.beem.tastymap.data.remote.AuthWebSocketClient
import org.beem.tastymap.data.remote.FileRemoteDataSource
import org.beem.tastymap.data.remote.HealthDataSource
import org.beem.tastymap.data.remote.UserSecurityDataSource
import org.beem.tastymap.data.remote.profile.MyProfileDataSource
import org.beem.tastymap.data.remote.profile.ProfileDataSource
import org.beem.tastymap.data.repository.AuthRepository
import org.beem.tastymap.data.repository.HealthRepository
import org.beem.tastymap.data.repository.UserSecurityRepository
import org.beem.tastymap.data.repository.profile.MyProfileRepository
import org.beem.tastymap.data.repository.profile.ProfileRepository
import org.beem.tastymap.database.TastyDatabase
import org.beem.tastymap.domain.auth.ClearSessionUseCase
import org.beem.tastymap.ui.auth.forgotPassword.ForgotScreenModel
import org.beem.tastymap.ui.auth.forgotPassword.PasswordResetSessionManager
import org.beem.tastymap.ui.auth.forgotPassword.ResetScreenModel
import org.beem.tastymap.ui.auth.logReg.LogRegScreenModel
import org.beem.tastymap.ui.splash.SplashScreenModel
import org.beem.tastymap.ui.auth.verification.email.EmailScreenModel
import org.beem.tastymap.ui.auth.verification.loginPending.PendingScreenModel
import org.beem.tastymap.ui.profile.health.HealthScreenModel
import org.beem.tastymap.ui.profile.myprofile.MyProfileScreenModel
import org.beem.tastymap.ui.profile.myprofile.settings.SettingsScreenModel
import org.beem.tastymap.ui.profile.myprofile.settings.activedevices.ActiveDevicesScreenModel
import org.beem.tastymap.ui.profile.myprofile.settings.changepassword.ChangePasswordScreenModel
import org.beem.tastymap.ui.profile.otherprofile.ProfileScreenModel
import org.koin.core.qualifier.named

val appModule = module {

    single<TokenManager> { TokenManagerImpl(get()) }
    single<SettingsManager> { SettingsManagerImpl(get()) }

    single<HttpClient>(named("noAuth")) {
        createNoAuthClient(get())
    }


    single<HttpClient>(named("auth")) {
        val factory = get<HttpClientFactory>()
        val noAuth = get<HttpClient>(named("noAuth"))
        factory.createAuthClient(noAuth)
    }
    single { AuthEventBus() }
    single { TastyDatabase(driver = get()) }
    single { get<TastyDatabase>().profileEntityQueries }
    single { ProfileMemoryCache() }
    single { HealthMemoryCache() }
    single { CacheManager(get(), get()) }

    single { ProfileLocalDataSource(get()) }
    factory { ClearSessionUseCase(get(), get(), get(), get()) }

    single { AuthDataSource(get(named("noAuth"))) }
    single { UserSecurityDataSource(get(named("noAuth"))) }
    single { HealthDataSource(get(named("auth"))) }
    single { ProfileDataSource(get(named("auth"))) }
    single { MyProfileDataSource(get(named("auth"))) }
    single { FileRemoteDataSource(get(named("auth"))) }

    single { AuthRepository(get(), get(), get(), get()) }
    single { UserSecurityRepository(get()) }
    single { HealthRepository(get(),get(),get()) }
    single { AuthWebSocketClient(get(named("auth"))) }
    single { PasswordResetSessionManager() }
    single { ProfileRepository(get(), get(), get()) }
    single { MyProfileRepository(get(), get(), get(), get(), get(),get()) }

    factory { LogRegScreenModel(get(), get(), get(), get()) }
    factory { PendingScreenModel(get(), get(), get(), get()) }
    factory { EmailScreenModel(get(), get()) }
    factory { SplashScreenModel(get()) }
    factory { ForgotScreenModel(get(), get(), get(), get()) }
    factory { ResetScreenModel(get()) }
    factory { HealthScreenModel(get()) }
    factory { ProfileScreenModel(get()) }
    single { MyProfileScreenModel(get()) }
    factory { SettingsScreenModel(get(),get(),get()) }
    factory { ActiveDevicesScreenModel(get()) }
    factory { ChangePasswordScreenModel(get(),get()) }

    single<VerifyNavigator> { MobileVerifyNavigator() }
}
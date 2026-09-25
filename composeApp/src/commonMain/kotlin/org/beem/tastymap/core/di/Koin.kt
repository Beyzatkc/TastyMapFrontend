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
import org.beem.tastymap.core.network.AuthHttpClientManager
import org.beem.tastymap.core.provider.AppDispatchers
import org.beem.tastymap.core.provider.DispatcherProvider
import org.beem.tastymap.core.provider.HttpClientFactory
import org.beem.tastymap.data.cache.*
import org.beem.tastymap.data.local.PostLocalDataSource
import org.beem.tastymap.data.local.ProfileLocalDataSource
import org.beem.tastymap.data.local.SearchHistoryLocalDataSource
import org.beem.tastymap.data.local.VisitLocalDataSource
import org.beem.tastymap.data.remote.*
import org.beem.tastymap.data.remote.profile.MyProfileDataSource
import org.beem.tastymap.data.remote.profile.ProfileDataSource
import org.beem.tastymap.data.repository.*
import org.beem.tastymap.data.repository.profile.MyProfileRepository
import org.beem.tastymap.data.repository.profile.ProfileRepository
import org.beem.tastymap.database.TastyDatabase
import org.beem.tastymap.domain.auth.ClearSessionUseCase
import org.beem.tastymap.domain.usecase.ToggleBlockUseCase
import org.beem.tastymap.domain.usecase.ToggleFollowUseCase
import org.beem.tastymap.ui.auth.forgotPassword.ForgotScreenModel
import org.beem.tastymap.ui.auth.forgotPassword.PasswordResetSessionManager
import org.beem.tastymap.ui.auth.forgotPassword.ResetScreenModel
import org.beem.tastymap.ui.auth.logReg.LogRegScreenModel
import org.beem.tastymap.ui.splash.SplashScreenModel
import org.beem.tastymap.ui.auth.verification.email.EmailScreenModel
import org.beem.tastymap.ui.auth.verification.loginPending.PendingScreenModel
import org.beem.tastymap.ui.common.NotificationBadgeManager
import org.beem.tastymap.ui.post.create.CreatePostScreenModel
import org.beem.tastymap.ui.post.detail.PostDetailScreenModel
import org.beem.tastymap.ui.post.mypost.MyPostScreenModel
import org.beem.tastymap.ui.post.otherpost.PostScreenModel
import org.beem.tastymap.ui.profile.health.HealthScreenModel
import org.beem.tastymap.ui.profile.myprofile.MyProfileScreenModel
import org.beem.tastymap.ui.profile.myprofile.editprofile.EditProfileScreenModel
import org.beem.tastymap.ui.profile.myprofile.notification.NotificationScreenModel
import org.beem.tastymap.ui.profile.myprofile.settings.SettingsScreenModel
import org.beem.tastymap.ui.profile.myprofile.settings.activedevices.ActiveDevicesScreenModel
import org.beem.tastymap.ui.profile.myprofile.settings.blockedusers.BlockedScreenModel
import org.beem.tastymap.ui.profile.myprofile.settings.changepassword.ChangePasswordScreenModel
import org.beem.tastymap.ui.profile.myprofile.settings.deleteaccount.DeleteAccountScreenModel
import org.beem.tastymap.ui.profile.myprofile.visit.VisitScreenModel
import org.beem.tastymap.ui.profile.otherprofile.ProfileScreenModel
import org.beem.tastymap.ui.profile.subscribers.SubscribersListScreenModel
import org.beem.tastymap.ui.search.SearchScreenModel
import org.koin.core.qualifier.named

val appModule = module {

    single<TokenManager> { TokenManagerImpl(get()) }
    single<SettingsManager> { SettingsManagerImpl(get()) }
    single<DispatcherProvider> { AppDispatchers }

    single<HttpClient>(named("noAuth")) {
        createNoAuthClient(get())
    }

    single {
        AuthHttpClientManager(
            factory = get<HttpClientFactory>(),
            noAuthClient = get(named("noAuth"))
        )
    }

    single { AuthEventBus() }
    single { TastyDatabase(driver = get()) }
    single { get<TastyDatabase>().profileEntityQueries }
    single { get<TastyDatabase>().searchHistoryEntityQueries }
    single { get<TastyDatabase>().visitEntityQueries }
    single { get<TastyDatabase>().postEntityQueries }

    // Caches
    single { ProfileMemoryCache() }
    single { HealthMemoryCache() }
    single { SubscribeMemoryCache() }
    single { SearchMemoryCache() }
    single { NotificationsMemoryCache() }
    single { BlockedMemoryCache() }
    single { VisitMemoryCache() }
    single { PostMemoryCache() }
    single { CacheManager(get(), get(), get(), get(), get(), get(),get(),get()) }

    single { ToggleFollowUseCase(get(), get()) }
    single { ToggleBlockUseCase(get(), get()) }
    factory { ClearSessionUseCase(get(), get(), get(), get(), get(), get()) }


    single { ProfileLocalDataSource(get(), get()) }
    single { SearchHistoryLocalDataSource(get(), get()) }
    single { VisitLocalDataSource(get(), get()) }
    single { PostLocalDataSource(get(), get()) }


    single { AuthDataSource(get(named("noAuth"))) }
    single { UserSecurityDataSource(get(named("noAuth"))) }


    single { HealthDataSource(get()) }
    single { ProfileDataSource(get()) }
    single { MyProfileDataSource(get()) }
    single { FileRemoteDataSource(get()) }
    single { SubscribersDataSource(get()) }
    single { SocialNotificationDataSource(get()) }
    single { SearchUserDataSource(get()) }
    single { BlockDataSource(get()) }
    single { DeleteAccountDataSource(get()) }
    single { AuthWebSocketClient(get()) }
    single { VisitDataSource(get()) }
    single { PostDataSource(get()) }

    single { NotificationBadgeManager() }

    single { AuthRepository(get(), get(), get(), get(),get()) }
    single { UserSecurityRepository(get()) }
    single { SubscribersRepository(get(), get(), get()) }
    single { HealthRepository(get(), get(), get()) }
    single { PasswordResetSessionManager() }
    single { ProfileRepository(get(), get(), get(), get()) }
    single { MyProfileRepository(get(), get(), get(), get(), get(), get(), get()) }
    single { SocialNotificationsRepository(get(), get()) }
    single { SearchUserRepository(get(), get(), get(), get()) }
    single { BlockRepository(get(), get(), get(), get(), get()) }
    single { DeleteAccountRepository(get(), get(), get()) }
    single { VisitRepository(get(), get(), get()) }
    single { PostRepository(get(), get(), get(),get(),get(),get()) }

    factory { LogRegScreenModel(get(), get(), get(), get()) }
    factory { PendingScreenModel(get(), get(), get(), get()) }
    factory { EmailScreenModel(get(), get()) }
    factory { SplashScreenModel(get()) }
    factory { ForgotScreenModel(get(), get(), get(), get()) }
    factory { ResetScreenModel(get()) }
    factory { HealthScreenModel(get()) }
    factory { SubscribersListScreenModel(get(), get()) }
    factory { VisitScreenModel(get()) }
    factory { MyPostScreenModel(get()) }
    factory { PostScreenModel(get()) }
    factory { PostDetailScreenModel(get()) }
    factory { CreatePostScreenModel(get(),get()) }

    factory { (userId: Long) ->
        ProfileScreenModel(
            userId = userId,
            repo = get(),
            toggleFollowUseCase = get(),
            toggleBlockUseCase = get()
        )
    }
    factory { MyProfileScreenModel(get(), get(), get()) }
    factory { EditProfileScreenModel(get()) }
    factory { SettingsScreenModel(get(), get(), get(), get()) }
    factory { ActiveDevicesScreenModel(get()) }
    factory { ChangePasswordScreenModel(get(), get()) }
    factory { SearchScreenModel(get()) }
    factory { NotificationScreenModel(get(), get(), get()) }
    factory { BlockedScreenModel(get(), get()) }
    factory { DeleteAccountScreenModel(get(), get()) }

    single<VerifyNavigator> { MobileVerifyNavigator() }
}
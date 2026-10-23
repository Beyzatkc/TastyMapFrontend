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
import org.beem.tastymap.data.local.HealthLocalDataSource
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
import org.beem.tastymap.ui.auth.forgotPassword.ForgotViewModel
import org.beem.tastymap.ui.auth.forgotPassword.PasswordResetSessionManager
import org.beem.tastymap.ui.auth.forgotPassword.ResetViewModel
import org.beem.tastymap.ui.auth.logReg.LogRegViewModel
import org.beem.tastymap.ui.splash.SplashViewModel
import org.beem.tastymap.ui.auth.verification.email.EmailViewModel
import org.beem.tastymap.ui.auth.verification.loginPending.PendingViewModel
import org.beem.tastymap.ui.bottomnav.MainViewModel
import org.beem.tastymap.ui.common.NotificationBadgeManager
import org.beem.tastymap.ui.post.create.CreatePostViewModel
import org.beem.tastymap.ui.post.detail.PostDetailViewModel
import org.beem.tastymap.ui.post.mypost.MyPostViewModel
import org.beem.tastymap.ui.post.mypost.updatepost.EditPostViewModel
import org.beem.tastymap.ui.post.otherpost.PostViewModel
import org.beem.tastymap.ui.post.postcomment.CommentViewModel
import org.beem.tastymap.ui.post.postlike.PostLikesViewModel
import org.beem.tastymap.ui.profile.health.HealthViewModel
import org.beem.tastymap.ui.profile.myprofile.MyProfileViewModel
import org.beem.tastymap.ui.profile.myprofile.editprofile.EditProfileViewModel
import org.beem.tastymap.ui.profile.myprofile.notification.NotificationViewModel
import org.beem.tastymap.ui.profile.myprofile.settings.SettingsViewModel
import org.beem.tastymap.ui.profile.myprofile.settings.activedevices.ActiveDevicesViewModel
import org.beem.tastymap.ui.profile.myprofile.settings.blockedusers.BlockedViewModel
import org.beem.tastymap.ui.profile.myprofile.settings.changepassword.ChangePasswordViewModel
import org.beem.tastymap.ui.profile.myprofile.settings.deleteaccount.DeleteAccountViewModel
import org.beem.tastymap.ui.profile.myprofile.visit.VisitViewModel
import org.beem.tastymap.ui.profile.otherprofile.ProfileViewModel
import org.beem.tastymap.ui.profile.subscribers.SubscribersListViewModel
import org.beem.tastymap.ui.search.SearchViewModel
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
    single { get<TastyDatabase>().healthEntityQueries }


    single { ProfileMemoryCache() }
    single { HealthMemoryCache() }
    single { SubscribeMemoryCache() }
    single { SearchMemoryCache() }
    single { NotificationsMemoryCache() }
    single { BlockedMemoryCache() }
    single { VisitMemoryCache() }
    single { PostCommentMemoryCache() }
    single { PostMemoryCache() }
    single { CacheManager(get(), get(), get(), get(), get(), get(),get(),get()) }

    single { ToggleFollowUseCase(get(), get()) }
    single { ToggleBlockUseCase(get(), get()) }
    factory { ClearSessionUseCase(get(), get(), get(), get(), get(), get()) }


    single { ProfileLocalDataSource(get(), get()) }
    single { SearchHistoryLocalDataSource(get(), get()) }
    single { VisitLocalDataSource(get(), get()) }
    single { PostLocalDataSource(get(), get()) }
    single { HealthLocalDataSource(get(),get()) }


    single { AuthDataSource(get(named("noAuth"))) }
    single { UserSecurityDataSource(get(named("noAuth"))) }


    single { HealthDataSource(get()) }
    single { PostCommentDataSource(get()) }
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
    single { SubscribersRepository(get(), get(), get(),get()) }
    single { HealthRepository(get(), get(), get(),get(),get()) }
    single { PasswordResetSessionManager() }
    single { ProfileRepository(get(), get(), get(), get()) }
    single { MyProfileRepository(get(), get(), get(), get(), get(), get(), get()) }
    single { SocialNotificationsRepository(get(), get()) }
    single { SearchUserRepository(get(), get(), get(), get()) }
    single { BlockRepository(get(), get(), get(), get(), get()) }
    single { DeleteAccountRepository(get(), get(), get()) }
    single { VisitRepository(get(), get(), get()) }
    single { PostRepository(get(), get(), get(),get(),get(),get()) }
    factory { PostCommentRepository(get(),get(),get(),get()) }

    factory { LogRegViewModel(get(), get(), get(), get()) }
    factory { PendingViewModel(get(), get(), get(), get()) }
    factory { EmailViewModel(get(), get()) }
    factory { SplashViewModel(get()) }
    factory { ForgotViewModel(get(), get(), get(), get()) }
    factory { ResetViewModel(get()) }
    factory { HealthViewModel(get()) }
    factory { SubscribersListViewModel(get(), get()) }
    factory { VisitViewModel(get()) }
    factory { MyPostViewModel(get()) }
    factory { PostViewModel(get()) }
    factory { PostDetailViewModel(get()) }
    factory { CommentViewModel(get(),get()) }

    factory { CreatePostViewModel(get(),get()) }
    factory { PostLikesViewModel(get(),get()) }
    factory { EditPostViewModel(get()) }

    factory { (userId: Long) ->
        ProfileViewModel(
            userId = userId,
            repo = get(),
            toggleFollowUseCase = get(),
            toggleBlockUseCase = get()
        )
    }
    factory { MyProfileViewModel(get()) }
    factory { MainViewModel(get(),get()) }
    factory { EditProfileViewModel(get()) }
    factory { SettingsViewModel(get(), get(), get(), get()) }
    factory { ActiveDevicesViewModel(get()) }
    factory { ChangePasswordViewModel(get(), get()) }
    factory { SearchViewModel(get()) }
    factory { NotificationViewModel(get(), get(), get()) }
    factory { BlockedViewModel(get(), get()) }
    factory { DeleteAccountViewModel(get(), get()) }

    single<VerifyNavigator> { MobileVerifyNavigator() }
}
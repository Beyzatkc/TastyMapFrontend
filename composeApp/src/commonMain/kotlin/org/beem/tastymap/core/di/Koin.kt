package org.beem.tastymap.core.di

import org.koin.dsl.module
import com.russhwolf.settings.Settings
import createAuthClient
import createNoAuthClient
import org.beem.tastymap.core.local.TokenManager
import org.beem.tastymap.core.local.TokenManagerImpl
import org.beem.tastymap.core.local.UserManager
import org.beem.tastymap.core.local.UserManagerImpl
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
    single<UserManager> { UserManagerImpl(get()) }

    single(named("noAuth")) { createNoAuthClient() }
    single(named("auth")) { createAuthClient(get(), get(named("noAuth"))) }

    single { AuthDataSource(get(named("noAuth"))) }
    single { UserDataSource(get(named("auth"))) }

    single { AuthRepository(get(), get(),get()) }
    single { PostRepository(get()) }

    factory { AuthScreenModel(get(),get()) }
    factory { PostScreenModel (get())}
    factory { SplashScreenModel(get()) }
}
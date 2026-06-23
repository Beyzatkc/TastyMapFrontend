package org.beem.tastymap.core.di

import org.koin.dsl.module
import com.russhwolf.settings.Settings
import createAuthClient
import createNoAuthClient
import org.beem.tastymap.core.local.TokenManager
import org.beem.tastymap.core.local.TokenManagerImpl
import org.beem.tastymap.data.remote.AuthDataSource
import org.beem.tastymap.data.repository.AuthRepository
import org.beem.tastymap.map.MapScreenModel
import org.beem.tastymap.map.api.MapDataSource
import org.beem.tastymap.map.repository.MapRepository
import org.beem.tastymap.place.RestaurantDetailScreenModel
import org.beem.tastymap.place.api.PlaceDataSource
import org.beem.tastymap.place.repository.PlaceRepository
import org.beem.tastymap.ui.auth.AuthScreenModel
import org.koin.core.qualifier.named

val appModule = module {
    single { Settings() }

    single<TokenManager> { TokenManagerImpl(get()) }

    single(named("noAuth")) { createNoAuthClient() }
    single(named("auth")) { createAuthClient(get(), get(named("noAuth"))) }

    single { AuthDataSource(get(named("noAuth"))) }
    //single { UserDataSource(get(named("auth"))) }

    single { AuthRepository(get(), get()) }
    factory { AuthScreenModel(get(),get()) }

    single { MapDataSource(get(named("auth"))) }
    single { MapRepository(get()) }
    factory { MapScreenModel(get(), get()) }

    single { PlaceDataSource(get(named("auth"))) }
    single { PlaceRepository(get()) }
    factory { RestaurantDetailScreenModel(get()) }

}
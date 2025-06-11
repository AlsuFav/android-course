package ru.fav.starlight.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.fav.starlight.navigation.Nav
import ru.fav.starlight.navigation.NavImpl
import ru.fav.starlight.navigation.NavMain
import ru.fav.starlight.navigation.NavMainImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface NavigationModule {

    @Binds
    @Singleton
    fun bindNavToImpl(impl: NavImpl): Nav

    @Binds
    @Singleton
    fun bindNavMainToImpl(impl: NavMainImpl): NavMain
}

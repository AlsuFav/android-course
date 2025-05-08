package ru.fav.starlight.di.module

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.fav.starlight.presentation.navigation.Nav
import ru.fav.starlight.presentation.navigation.NavMain
import ru.fav.starlight.presentation.navigation.impl.NavImpl
import ru.fav.starlight.presentation.navigation.impl.NavMainImpl
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

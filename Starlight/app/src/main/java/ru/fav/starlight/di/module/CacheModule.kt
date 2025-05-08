package ru.fav.starlight.di.module

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.fav.starlight.data.cache.NasaImageDetailsCacheManagerImpl
import ru.fav.starlight.data.cache.NasaImagesCacheManagerImpl
import ru.fav.starlight.domain.cache.NasaImageDetailsCacheManager
import ru.fav.starlight.domain.cache.NasaImagesCacheManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface CacheModule {

    @Binds
    @Singleton
    fun bindNasaImagesCacheManagerToImpl(impl: NasaImagesCacheManagerImpl): NasaImagesCacheManager

    @Binds
    @Singleton
    fun bindNasaImageDetailsCacheManagerToImpl(
        impl: NasaImageDetailsCacheManagerImpl
    ): NasaImageDetailsCacheManager
}

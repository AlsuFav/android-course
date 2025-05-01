package ru.fav.starlight.di.module

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.fav.starlight.data.provider.ResourceProviderImpl
import ru.fav.starlight.data.provider.ApiKeyProviderImpl
import ru.fav.starlight.data.provider.DateProviderImpl
import ru.fav.starlight.data.repository.ApiKeyRepositoryImpl
import ru.fav.starlight.data.repository.NasaImagesRepositoryImpl
import ru.fav.starlight.domain.provider.ApiKeyProvider
import ru.fav.starlight.domain.provider.DateProvider
import ru.fav.starlight.domain.provider.ResourceProvider
import ru.fav.starlight.domain.repository.ApiKeyRepository
import ru.fav.starlight.domain.repository.NasaImagesRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface BinderModule {

    @Binds
    @Singleton
    fun bindNasaImagesRepositoryToImpl(impl: NasaImagesRepositoryImpl): NasaImagesRepository

    @Binds
    @Singleton
    fun bindApiKeyRepositoryToImpl(impl: ApiKeyRepositoryImpl): ApiKeyRepository

    @Binds
    @Singleton
    fun bindResourceProviderToImpl(impl: ResourceProviderImpl): ResourceProvider

    @Binds
    @Singleton
    fun bindApiKeyProviderToImpl(impl: ApiKeyProviderImpl): ApiKeyProvider


    @Binds
    @Singleton
    fun bindDateProviderToImpl(impl: DateProviderImpl): DateProvider
}

package ru.fav.starlight.domain.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import ru.fav.starlight.domain.di.qualifier.IoDispatchers

@Module
@InstallIn(SingletonComponent::class)
class DomainModule {

    @Provides
    @IoDispatchers
    fun provideIoDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }
}
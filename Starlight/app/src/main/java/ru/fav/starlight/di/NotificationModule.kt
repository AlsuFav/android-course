package ru.fav.starlight.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import ru.fav.starlight.notification.FcmMessageHandler
import ru.fav.starlight.notification.NotificationChannelsProvider
import ru.fav.starlight.notification.NotificationChannelsProviderImpl
import ru.fav.starlight.notification.NotificationHelper
import ru.fav.starlight.notification.NotificationHelperImpl
import ru.fav.starlight.notification.handler.ConditionalFeatureHandler
import ru.fav.starlight.notification.handler.HighPriorityNotificationHandler
import ru.fav.starlight.notification.handler.SaveDataHandler
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface NotificationModule {

    @Binds
    @Singleton
    fun bindNotificationChannelsProviderToImpl(impl: NotificationChannelsProviderImpl): NotificationChannelsProvider

    @Binds
    @Singleton
    fun bindNotificationHelperToImpl(impl: NotificationHelperImpl): NotificationHelper

    @Binds
    @IntoSet
    abstract fun bindHighPriorityHandler(handler: HighPriorityNotificationHandler): FcmMessageHandler

    @Binds
    @IntoSet
    abstract fun bindSaveDataHandler(handler: SaveDataHandler): FcmMessageHandler

    @Binds
    @IntoSet
    abstract fun bindConditionalFeatureHandler(handler: ConditionalFeatureHandler): FcmMessageHandler
}

package ru.fav.starlight.notification

import ru.fav.starlight.notification.model.NotificationChannelModel
import ru.fav.starlight.notification.util.NotificationType

interface NotificationChannelsProvider {
    fun provideChannels(): List<NotificationChannelModel>
    fun getChannelForType(type: NotificationType): NotificationChannelModel
}
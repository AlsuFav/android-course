package ru.fav.starlight.notification

import android.app.NotificationManager
import ru.fav.starlight.app.R
import ru.fav.starlight.notification.mapper.NotificationChannelMapper
import ru.fav.starlight.notification.model.NotificationChannelModel
import ru.fav.starlight.notification.util.NotificationType
import javax.inject.Inject

class NotificationChannelsProviderImpl @Inject constructor(
    private val mapper: NotificationChannelMapper,
) : NotificationChannelsProvider {

    override fun provideChannels(): List<NotificationChannelModel> = listOf(
        mapper.map(
            nameRes = R.string.default_importance,
            importance = NotificationManager.IMPORTANCE_DEFAULT,
            type = NotificationType.DEFAULT
        ),
        mapper.map(
            nameRes = R.string.high_importance,
            importance = NotificationManager.IMPORTANCE_HIGH,
            type = NotificationType.HIGH
        )
    )

    override fun getChannelForType(type: NotificationType): NotificationChannelModel {
        return provideChannels().first { it.id == type.name.lowercase() }
    }
}
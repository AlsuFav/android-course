package ru.fav.starlight.notification.handler

import ru.fav.starlight.app.R
import ru.fav.starlight.domain.provider.ResourceProvider
import ru.fav.starlight.notification.FcmMessageHandler
import ru.fav.starlight.notification.NotificationHelper
import ru.fav.starlight.notification.model.NotificationModel
import ru.fav.starlight.notification.util.FcmCategories
import ru.fav.starlight.notification.util.FcmDataKeys
import ru.fav.starlight.notification.util.NotificationType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HighPriorityNotificationHandler @Inject constructor(
    private val notificationHelper: NotificationHelper,
    private val resourceProvider: ResourceProvider
) : FcmMessageHandler {

    override fun canHandle(category: String): Boolean = 
        category == FcmCategories.HIGH_PRIORITY_NOTIFICATION

    override fun handle(data: Map<String, String>) {
        val title = data[FcmDataKeys.TITLE] ?: resourceProvider.getString(R.string.default_notification_title)
        val message = data[FcmDataKeys.MESSAGE] ?: resourceProvider.getString(R.string.default_notification_message)
        val notificationId = data[FcmDataKeys.ID]?.toIntOrNull() ?: System.currentTimeMillis().toInt()

        val notificationData = NotificationModel(
            id = notificationId,
            title = title,
            message = message,
            notificationType = NotificationType.HIGH
        )
        notificationHelper.showNotification(notificationData)
    }
}
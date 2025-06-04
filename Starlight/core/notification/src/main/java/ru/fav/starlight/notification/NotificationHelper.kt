package ru.fav.starlight.notification

import android.app.PendingIntent
import ru.fav.starlight.notification.model.NotificationModel

interface NotificationHelper {
    fun createChannelsIfNeeded()
    fun showNotification(notification: NotificationModel, pendingIntent: PendingIntent? = null)
}
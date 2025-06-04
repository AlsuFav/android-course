package ru.fav.starlight.notification.model

import ru.fav.starlight.notification.util.NotificationType

data class NotificationModel(
    val id: Int,
    val title: String,
    val message: String,
    val notificationType: NotificationType
)
package ru.fav.starlight.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.fav.starlight.MainActivity
import ru.fav.starlight.notification.model.NotificationModel
import javax.inject.Inject


class NotificationHelperImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationChannelsProvider: NotificationChannelsProvider
) : NotificationHelper {

    private val notificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun createChannelsIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannelsProvider.provideChannels().forEach { channel ->
                if (notificationManager.getNotificationChannel(channel.id) == null) {
                    notificationManager.createNotificationChannel(
                        NotificationChannel(
                            channel.id,
                            channel.name,
                            channel.importance
                        )
                    )
                }
            }
        }
    }

    override fun showNotification(notification: NotificationModel, pendingIntent: PendingIntent?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val channel = notificationChannelsProvider.getChannelForType(notification.notificationType)
        val channelId = channel.id

        val finalPendingIntent: PendingIntent = pendingIntent ?: run {
            val defaultIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            PendingIntent.getActivity(
                context,
                0,
                defaultIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setContentTitle(notification.title)
            .setSmallIcon(ru.fav.starlight.presentation.R.drawable.ic_starlight)
            .setContentText(notification.message)
            .setPriority(channel.importance)
            .setContentIntent(finalPendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_ALARM)

        NotificationManagerCompat.from(context).notify(notification.id, builder.build())
    }
}
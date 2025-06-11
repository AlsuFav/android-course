package ru.fav.starlight

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import ru.fav.starlight.notification.NotificationHelper
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {
    @Inject
    lateinit var appLifecycleObserver: AppLifecycleObserver

    @Inject
    lateinit var notificationHelper: NotificationHelper

    override fun onCreate() {
        super.onCreate()
        appLifecycleObserver.register()
        notificationHelper.createChannelsIfNeeded()
    }
}

package ru.fav.starlight.notification

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import ru.fav.starlight.notification.util.FcmDataKeys
import javax.inject.Inject

@AndroidEntryPoint
class AppMessagingService @Inject constructor() : FirebaseMessagingService() {

    @Inject
    lateinit var handlers: Set<@JvmSuppressWildcards FcmMessageHandler>

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if (remoteMessage.data.isNotEmpty()) {
            handleDataMessage(remoteMessage.data)
        }
    }

    private fun handleDataMessage(data: Map<String, String>) {
        val category = data[FcmDataKeys.CATEGORY] ?: return

        handlers.firstOrNull { it.canHandle(category) }?.handle(data)
    }
}
package ru.fav.starlight.notification.handler

import android.os.Bundle
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import ru.fav.starlight.AppEvent
import ru.fav.starlight.AppEventBus
import ru.fav.starlight.AppLifecycleObserver
import ru.fav.starlight.app.R
import ru.fav.starlight.domain.di.qualifier.IoDispatchers
import ru.fav.starlight.domain.exception.NoApiKeyException
import ru.fav.starlight.domain.usecase.GetApiKeyUseCase
import ru.fav.starlight.notification.FcmMessageHandler
import ru.fav.starlight.notification.util.FcmCategories
import ru.fav.starlight.notification.util.FcmDataKeys
import ru.fav.starlight.notification.util.FcmFeatureNames
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConditionalFeatureHandler @Inject constructor(
    private val appLifecycleObserver: AppLifecycleObserver,
    private val getApiKeyUseCase: GetApiKeyUseCase,
    private val appEventBus: AppEventBus,
    @IoDispatchers private val dispatcher: CoroutineDispatcher
) : FcmMessageHandler {

    override fun canHandle(category: String): Boolean = 
        category == FcmCategories.OPEN_FEATURE_IF_APP_OPEN

    override fun handle(data: Map<String, String>) {
        if (!appLifecycleObserver.isAppInForeground) return

        val featureName = data[FcmDataKeys.FEATURE_NAME]
        val (featureActionId, requiresAuth) = when (featureName) {
            FcmFeatureNames.PROFILE -> R.id.action_global_profile to true
            else -> return
        }

        val argsJson = data[FcmDataKeys.FEATURE_ARGS]
        val args = argsJson?.let { parseJsonToBundle(it) }

        CoroutineScope(dispatcher).launch {
            val authorized = if (requiresAuth) {
                try {
                    getApiKeyUseCase()
                    true
                } catch (_: NoApiKeyException) {
                    false
                }
            } else true

            appEventBus.postEvent(
                AppEvent.ConditionalFeatureRequest(
                    featureActionId = featureActionId,
                    args = args,
                    authorized = authorized,
                )
            )
        }
    }

    private fun parseJsonToBundle(jsonString: String): Bundle? {
        return try {
            val jsonObject = JSONObject(jsonString)
            val bundle = Bundle()
            jsonObject.keys().forEach { key ->
                when (val value = jsonObject.get(key)) {
                    is String -> bundle.putString(key, value)
                    is Int -> bundle.putInt(key, value)
                    is Boolean -> bundle.putBoolean(key, value)
                    is Long -> bundle.putLong(key, value)
                    is Double -> bundle.putDouble(key, value)
                    else -> null
                }
            }
            bundle
        } catch (_: JSONException) {
            null
        }
    }
}
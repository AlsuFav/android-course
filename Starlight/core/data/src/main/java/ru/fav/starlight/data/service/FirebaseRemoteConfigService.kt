package ru.fav.starlight.data.service

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.coroutines.tasks.await
import ru.fav.starlight.data.util.FeatureFlagType
import ru.fav.starlight.data.util.FeatureFlags
import ru.fav.starlight.domain.service.RemoteConfigService
import ru.fav.starlight.network.BuildConfig
import javax.inject.Inject

class FirebaseRemoteConfigService @Inject constructor(
    private val firebaseRemoteConfig: FirebaseRemoteConfig
) : RemoteConfigService {

    init {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 0L else 3600L
        }
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings)

        val defaults = mutableMapOf<String, Any>()
        FeatureFlags.ALL_FLAGS.forEach { flag ->
            when (flag.type) {
                FeatureFlagType.BOOLEAN -> defaults[flag.name] = false
                FeatureFlagType.STRING -> defaults[flag.name] = ""
                FeatureFlagType.DOUBLE -> defaults[flag.name] = 0.0
            }
        }

        firebaseRemoteConfig.setDefaultsAsync(defaults)
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return firebaseRemoteConfig.getBoolean(key)
    }

    override fun getString(key: String, defaultValue: String): String {
        return firebaseRemoteConfig.getString(key)
    }

    override fun getDouble(key: String, defaultValue: Double): Double {
        return firebaseRemoteConfig.getDouble(key)
    }

    override suspend fun fetchAndActivate(): Boolean {
        return try {
            val updated = firebaseRemoteConfig.fetchAndActivate().await()
            updated
        } catch (_: Exception) {
            false
        }
    }
}
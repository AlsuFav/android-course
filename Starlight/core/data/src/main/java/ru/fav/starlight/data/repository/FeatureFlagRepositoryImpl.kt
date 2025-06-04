package ru.fav.starlight.data.repository

import ru.fav.starlight.data.util.FeatureFlags
import ru.fav.starlight.domain.service.RemoteConfigService
import ru.fav.starlight.domain.repository.FeatureFlagRepository
import javax.inject.Inject

class FeatureFlagRepositoryImpl @Inject constructor(
    private val remoteConfigService: RemoteConfigService
) : FeatureFlagRepository {

    override fun isDetailsAvailable(): Boolean {
        return remoteConfigService.getBoolean(FeatureFlags.DETAILS_AVAILABLE.name, false)
    }
}
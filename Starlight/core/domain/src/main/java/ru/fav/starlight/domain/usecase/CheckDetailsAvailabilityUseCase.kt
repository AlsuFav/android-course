package ru.fav.starlight.domain.usecase

import ru.fav.starlight.domain.repository.FeatureFlagRepository
import javax.inject.Inject

class CheckDetailsAvailabilityUseCase @Inject constructor(
    private val featureFlagRepository: FeatureFlagRepository
) {
    operator fun invoke(): Boolean {
        return featureFlagRepository.isDetailsAvailable()
    }
}
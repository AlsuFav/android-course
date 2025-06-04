package ru.fav.starlight.data.util

object FeatureFlags {
    val DETAILS_AVAILABLE = FeatureFlag(
        name = "details_available_flag",
        type = FeatureFlagType.BOOLEAN,
        comment = "Controls access to the details screen."
    )

    val ALL_FLAGS = listOf(DETAILS_AVAILABLE)
}
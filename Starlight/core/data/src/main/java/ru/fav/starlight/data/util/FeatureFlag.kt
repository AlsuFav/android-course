package ru.fav.starlight.data.util

data class FeatureFlag(
    val name: String,
    val type: FeatureFlagType,
    val comment: String
)
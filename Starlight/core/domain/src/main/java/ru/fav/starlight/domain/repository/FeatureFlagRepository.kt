package ru.fav.starlight.domain.repository

interface FeatureFlagRepository {
    fun isDetailsAvailable(): Boolean
}
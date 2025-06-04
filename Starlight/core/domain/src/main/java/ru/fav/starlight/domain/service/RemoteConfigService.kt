package ru.fav.starlight.domain.service

interface RemoteConfigService {
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean
    fun getString(key: String, defaultValue: String = ""): String
    fun getDouble(key: String, defaultValue: Double = 0.0): Double

    suspend fun fetchAndActivate() : Boolean
}
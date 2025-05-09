package ru.fav.starlight.domain.repository

interface ApiKeyRepository {
    suspend fun saveApiKey(apiKey: String)
    suspend fun getApiKey(): String?
    suspend fun clearApiKey()
}

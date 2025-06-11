package ru.fav.starlight.domain.repository

interface ApiKeyRepository {
    suspend fun saveApiKey(token: String)
    suspend fun getApiKey(): String?
    suspend fun clearApiKey()
}

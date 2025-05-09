package ru.fav.starlight.domain.provider

interface ApiKeyProvider {
    fun getApiKey(): String = ""
    fun setApiKey(newApiKey: String)
}

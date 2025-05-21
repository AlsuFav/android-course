package ru.fav.starlight.data.provider

import ru.fav.starlight.domain.provider.ApiKeyProvider
import javax.inject.Inject

class ApiKeyProviderImpl @Inject constructor() : ApiKeyProvider  {
    private var apiKey: String = ""

    override fun getApiKey(): String = apiKey

    override fun setApiKey(newApiKey: String) {
        apiKey = newApiKey
    }
}

package ru.fav.starlight.data.remote.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import ru.fav.starlight.BuildConfig
import ru.fav.starlight.domain.provider.ApiKeyProvider
import javax.inject.Inject

class ApiKeyInterceptor @Inject constructor(
    private val apiKeyProvider: ApiKeyProvider
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val apiKey = apiKeyProvider.getApiKey()

        val url = chain.request().url.newBuilder()
            .addQueryParameter("api_key", apiKey)

        val request = chain.request().newBuilder().url(url.build())

        return chain.proceed(request.build())
    }
}

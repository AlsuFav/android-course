package ru.fav.starlight.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.fav.starlight.di.qualifier.IoDispatchers
import ru.fav.starlight.domain.exception.NoApiKeyException
import ru.fav.starlight.domain.provider.ApiKeyProvider
import ru.fav.starlight.domain.repository.ApiKeyRepository
import javax.inject.Inject

class GetApiKeyUseCase @Inject constructor(
    private val repository: ApiKeyRepository,
    private val provider: ApiKeyProvider,
    @IoDispatchers private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke() {
        return withContext(dispatcher) {
            val key = repository.getApiKey()
            if (key == null) {
                throw NoApiKeyException(null)
            } else {
                provider.setApiKey(key)
            }
        }
    }
}

package ru.fav.starlight.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.fav.starlight.domain.di.qualifier.IoDispatchers
import ru.fav.starlight.domain.provider.ApiKeyProvider
import ru.fav.starlight.domain.repository.ApiKeyRepository
import javax.inject.Inject

class ClearApiKeyUseCase @Inject constructor(
    private val repository: ApiKeyRepository,
    private val provider: ApiKeyProvider,
    @IoDispatchers private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke() = withContext(dispatcher) {
        repository.clearApiKey()
        provider.setApiKey("")
    }
}

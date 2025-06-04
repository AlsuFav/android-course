package ru.fav.starlight.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.fav.starlight.domain.di.qualifier.IoDispatchers
import ru.fav.starlight.domain.service.RemoteConfigService
import javax.inject.Inject

class FetchRemoteConfigUseCase @Inject constructor(
    private val remoteConfigService: RemoteConfigService,
    @IoDispatchers private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke() : Boolean {
        return withContext(dispatcher) {
            remoteConfigService.fetchAndActivate()
        }
    }
}
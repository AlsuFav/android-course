package ru.fav.starlight.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.fav.starlight.di.qualifier.IoDispatchers
import ru.fav.starlight.domain.model.NasaImageDetailsModel
import ru.fav.starlight.domain.repository.NasaImagesRepository
import javax.inject.Inject

class GetNasaImageDetailsUseCase @Inject constructor(
    private val nasaImagesRepository: NasaImagesRepository,
    @IoDispatchers private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        date: String
    ): NasaImageDetailsModel {
        return withContext(dispatcher) {
            nasaImagesRepository.getNasaImageDetails(date)
        }
    }
}

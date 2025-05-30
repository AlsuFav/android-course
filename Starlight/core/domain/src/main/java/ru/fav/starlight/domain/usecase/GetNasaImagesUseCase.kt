package ru.fav.starlight.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.fav.starlight.domain.di.qualifier.IoDispatchers
import ru.fav.starlight.domain.model.NasaImageModel
import ru.fav.starlight.domain.repository.NasaImagesRepository
import javax.inject.Inject

class GetNasaImagesUseCase @Inject constructor(
    private val nasaImagesRepository: NasaImagesRepository,
    @IoDispatchers private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        startDate: String,
        endDate: String
    ): List<NasaImageModel> {
        return withContext(dispatcher) {
            nasaImagesRepository.getNasaImages(
                startDate = startDate,
                endDate = endDate
            )
        }
    }
}

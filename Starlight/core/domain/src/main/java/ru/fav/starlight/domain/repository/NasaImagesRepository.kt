package ru.fav.starlight.domain.repository

import ru.fav.starlight.domain.model.NasaImageDetailsModel
import ru.fav.starlight.domain.model.NasaImageModel

interface NasaImagesRepository {
    suspend fun getNasaImages(
        startDate: String,
        endDate: String
    ): List<NasaImageModel>

    suspend fun getNasaImageDetails(date: String): NasaImageDetailsModel
}

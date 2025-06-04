package ru.fav.starlight.domain.cache

import ru.fav.starlight.domain.model.NasaImageDetailsModel

interface NasaImageDetailsCacheManager {
    fun get(date: String): NasaImageDetailsModel?
    fun put(date: String, data: NasaImageDetailsModel)
}

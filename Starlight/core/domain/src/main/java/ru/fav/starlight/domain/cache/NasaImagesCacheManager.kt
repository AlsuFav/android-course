package ru.fav.starlight.domain.cache

import ru.fav.starlight.domain.model.NasaImageModel

interface NasaImagesCacheManager {
    fun get(startDate: String, endDate: String): List<NasaImageModel>?
    fun put(startDate: String, endDate: String, data: List<NasaImageModel>)
    fun generateKey(startDate: String, endDate: String): String
}

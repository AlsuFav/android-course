package ru.fav.starlight.data.cache.util

import ru.fav.starlight.domain.model.NasaImageModel

data class NasaImagesCacheEntry(
    val data: List<NasaImageModel>,
    var timestamp: Long,
    var otherRequestsSinceLastAccess: Int
)

package ru.fav.starlight.data.cache.util

import ru.fav.starlight.domain.model.NasaImageDetailsModel

data class NasaImageDetailsCacheEntry(
    val data: NasaImageDetailsModel,
    var timestamp: Long,
    var otherRequestsSinceLastAccess: Int
)

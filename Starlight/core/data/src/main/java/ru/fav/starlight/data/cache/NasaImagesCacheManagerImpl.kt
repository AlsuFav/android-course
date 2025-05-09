package ru.fav.starlight.data.cache

import ru.fav.starlight.data.cache.util.CacheConstants
import ru.fav.starlight.data.cache.util.NasaImageDetailsCacheEntry
import ru.fav.starlight.data.cache.util.NasaImagesCacheEntry
import ru.fav.starlight.domain.cache.NasaImagesCacheManager
import ru.fav.starlight.domain.model.NasaImageModel
import ru.fav.starlight.domain.provider.DateProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NasaImagesCacheManagerImpl @Inject constructor(
    private val dateProvider: DateProvider
) : NasaImagesCacheManager {

    private val cache = mutableMapOf<String, NasaImagesCacheEntry>()

    override fun generateKey(startDate: String, endDate: String): String {
        return "$startDate-$endDate"
    }

    @Synchronized
    override fun get(startDate: String, endDate: String): List<NasaImageModel>? {
        val key = generateKey(startDate, endDate)
        val currentTime = dateProvider.getCurrentDate().timeInMillis

        val entry = cache[key]

        return when {
            entry == null -> null

            (currentTime - entry.timestamp) > CacheConstants.CACHE_COOLDOWN_MILLIS -> {
                cache.remove(key)
                null
            }

            entry.otherRequestsSinceLastAccess >= CacheConstants.MAX_INTERSTITIAL_REQUESTS_BEFORE_INVALIDATION -> {
                cache.remove(key)
                null
            }

            else -> {
                incrementOtherCounters(key)

                entry.timestamp = currentTime
                entry.otherRequestsSinceLastAccess = 0
                entry.data
            }
        }
    }

    @Synchronized
    override fun put(startDate: String, endDate: String, data: List<NasaImageModel>) {
        val key = generateKey(startDate, endDate)
        val currentTime = dateProvider.getCurrentDate().timeInMillis

        incrementOtherCounters(key)

        cache[key] = NasaImagesCacheEntry(
            data = data,
            timestamp = currentTime,
            otherRequestsSinceLastAccess = 0
        )
    }

    private fun incrementOtherCounters(accessedKey: String) {
        cache.forEach { (entryKey, cacheEntry) ->
            if (entryKey != accessedKey) {
                cacheEntry.otherRequestsSinceLastAccess++
            }
        }
    }
}

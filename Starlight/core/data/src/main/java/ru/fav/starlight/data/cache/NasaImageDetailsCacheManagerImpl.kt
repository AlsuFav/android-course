package ru.fav.starlight.data.cache

import ru.fav.starlight.data.cache.util.CacheConstants
import ru.fav.starlight.data.cache.util.NasaImageDetailsCacheEntry
import ru.fav.starlight.domain.cache.NasaImageDetailsCacheManager
import ru.fav.starlight.domain.model.NasaImageDetailsModel
import ru.fav.starlight.domain.provider.DateProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NasaImageDetailsCacheManagerImpl @Inject constructor(
    private val dateProvider: DateProvider
) : NasaImageDetailsCacheManager {

    private val cache = mutableMapOf<String, NasaImageDetailsCacheEntry>()

    @Synchronized
    override fun get(date: String): NasaImageDetailsModel? {
        val currentTime = dateProvider.getCurrentDate().timeInMillis
        val entry = cache[date]

        return when {
            entry == null -> null

            (currentTime - entry.timestamp) > CacheConstants.CACHE_COOLDOWN_MILLIS -> {
                cache.remove(date)
                null
            }

            entry.otherRequestsSinceLastAccess >= CacheConstants.MAX_INTERSTITIAL_REQUESTS_BEFORE_INVALIDATION -> {
                cache.remove(date)
                null
            }

            else -> {
                incrementOtherCounters(date)

                entry.timestamp = currentTime
                entry.otherRequestsSinceLastAccess = 0
                entry.data
            }
        }
    }

    @Synchronized
    override fun put(date: String, data: NasaImageDetailsModel) {
        val currentTime = dateProvider.getCurrentDate().timeInMillis

        incrementOtherCounters(date)

        cache[date] = NasaImageDetailsCacheEntry(
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

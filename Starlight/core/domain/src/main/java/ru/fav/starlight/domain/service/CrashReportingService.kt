package ru.fav.starlight.domain.service

interface CrashReportingService {
    fun setUserId(userId: String)
    fun setCustomKey(key: String, value: String)
    fun setCustomKey(key: String, value: Boolean)
    fun setCustomKey(key: String, value: Double)

    fun recordException(throwable: Throwable)
    fun logMessage(message: String)
}
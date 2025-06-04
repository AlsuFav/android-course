package ru.fav.starlight.data.service

import com.google.firebase.crashlytics.FirebaseCrashlytics
import ru.fav.starlight.domain.service.CrashReportingService
import javax.inject.Inject

class FirebaseCrashReportingService @Inject constructor(
    private val firebaseCrashlytics: FirebaseCrashlytics
) : CrashReportingService {

    override fun setUserId(userId: String) {
        firebaseCrashlytics.setUserId(userId)
    }

    override fun setCustomKey(key: String, value: String) {
        firebaseCrashlytics.setCustomKey(key, value)
    }

    override fun setCustomKey(key: String, value: Boolean) {
        firebaseCrashlytics.setCustomKey(key, value)
    }

    override fun setCustomKey(key: String, value: Double) {
        firebaseCrashlytics.setCustomKey(key, value)
    }

    override fun recordException(throwable: Throwable) {
        firebaseCrashlytics.recordException(throwable)
    }

    override fun logMessage(message: String) {
        firebaseCrashlytics.log(message)
    }
}
package ru.fav.starlight.domain.usecase

import ru.fav.starlight.domain.service.CrashReportingService
import javax.inject.Inject

class SetDateAnalyticsUseCase @Inject constructor(
    private val crashReportingService: CrashReportingService
) {
    operator fun invoke(date: String) {
        crashReportingService.setCustomKey("date", date)
        crashReportingService.logMessage("User tried to load data for date: $date")
    }
}
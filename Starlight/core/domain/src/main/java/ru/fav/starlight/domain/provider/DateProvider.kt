package ru.fav.starlight.domain.provider

import java.util.Calendar

interface DateProvider {
    fun getCurrentDate(): Calendar
    fun formatDate(calendar: Calendar): String
    fun parseDate(dateString: String): Calendar
}

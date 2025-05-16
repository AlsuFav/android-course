package ru.fav.starlight.search.ui.state

import java.util.Calendar

sealed class SearchEffect {
    data class ShowDatePicker(
        val type: DateType,
        val minDateMillis: Long,
        val maxDateMillis: Long,
        val initialDate: Calendar
    ) : SearchEffect()
    data class ShowToast(val message: String) : SearchEffect()
}

enum class DateType { START, END }

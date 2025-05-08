package ru.fav.starlight.presentation.screen.search.state

sealed class SearchEffect {
    data class ShowDatePicker(val type: DateType, val maxDateMillis: Long) : SearchEffect()
    data class ShowToast(val message: String) : SearchEffect()
}

package ru.fav.starlight.presentation.screen.search.state

import java.util.Calendar

sealed class SearchEvent {
    object LoadInitialData : SearchEvent()
    object LoadCurrentTimeMillis : SearchEvent()
    object OnStartDateClicked : SearchEvent()
    object OnEndDateClicked : SearchEvent()
    data class OnFetchImagesClicked(val startDate: String, val endDate: String) : SearchEvent()
    data class OnDateSelected(val type: DateType, val calendar: Calendar) : SearchEvent()
    data class OnNasaImageClicked(val date: String) : SearchEvent()
}
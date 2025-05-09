package ru.fav.starlight.details.ui.state

sealed class DetailsEvent {
    object OnBackClicked : DetailsEvent()
    data class GetNasaImageDetails(val date: String) : DetailsEvent()
}

package ru.fav.starlight.presentation.screen.details.state

sealed class DetailsEvent {
    object OnBackClicked : DetailsEvent()
    data class GetNasaImageDetails(val date: String) : DetailsEvent()
}

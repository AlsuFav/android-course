package ru.fav.starlight.presentation.screen.details.state

sealed class DetailsEffect {
    data class ShowToast(val message: String) : DetailsEffect()
}

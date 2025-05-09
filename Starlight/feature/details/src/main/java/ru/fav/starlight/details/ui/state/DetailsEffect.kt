package ru.fav.starlight.details.ui.state

sealed class DetailsEffect {
    data class ShowToast(val message: String) : DetailsEffect()
}

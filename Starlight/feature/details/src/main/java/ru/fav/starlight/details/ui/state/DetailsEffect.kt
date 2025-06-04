package ru.fav.starlight.details.ui.state

sealed class DetailsEffect {
    data class ShowToast(val message: String) : DetailsEffect()
    data class ShowErrorDialog(val message: String) : DetailsEffect()
}

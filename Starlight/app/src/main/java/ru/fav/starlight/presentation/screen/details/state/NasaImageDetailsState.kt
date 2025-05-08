package ru.fav.starlight.presentation.screen.details.state

import ru.fav.starlight.domain.model.NasaImageDetailsModel

sealed class NasaImageDetailsState {
    object Loading : NasaImageDetailsState()
    data class Success(val nasaImage: NasaImageDetailsModel) : NasaImageDetailsState()
    data class Error(val message: String) : NasaImageDetailsState()
}

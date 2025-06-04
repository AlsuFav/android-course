package ru.fav.starlight.details.ui.state

import ru.fav.starlight.domain.model.NasaImageDetailsModel

sealed class NasaImageDetailsState {
    object Loading : NasaImageDetailsState()
    data class Success(val nasaImage: NasaImageDetailsModel) : NasaImageDetailsState()
    object Error : NasaImageDetailsState()
}

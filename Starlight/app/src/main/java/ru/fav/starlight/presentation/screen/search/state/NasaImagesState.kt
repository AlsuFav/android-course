package ru.fav.starlight.presentation.screen.search.state

import ru.fav.starlight.domain.model.NasaImageModel

sealed class NasaImagesState {
    object Initial : NasaImagesState()
    object Loading : NasaImagesState()
    data class Success(val nasaImages: List<NasaImageModel>) : NasaImagesState()

    sealed class Error : NasaImagesState() {
        data class FieldError(val message: String) : Error()
        data class GlobalError(val message: String) : Error()
    }
}
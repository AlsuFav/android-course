package ru.fav.starlight.data.mapper

import ru.fav.starlight.data.remote.pojo.NasaImageResponse
import ru.fav.starlight.domain.model.NasaImageDetailsModel
import ru.fav.starlight.domain.model.NasaImageModel
import javax.inject.Inject

class NasaImageResponseMapper @Inject constructor() {

    fun mapNasaImageDetails(input: NasaImageResponse?): NasaImageDetailsModel {
        return input?.let {
            NasaImageDetailsModel(
                title = it.title ?: "",
                date = it.date ?: "",
                explanation = it.explanation ?: "",
                hdImageUrl = it.hdImageUrl ?: "",
            )
        } ?: NasaImageDetailsModel()
    }

    private fun mapNasaImage(input: NasaImageResponse?): NasaImageModel {
        return input?.let {
            NasaImageModel(
                title = it.title ?: "",
                date = it.date ?: "",
                imageUrl = it.imageUrl ?: "",
            )
        } ?: NasaImageModel()
    }

    fun mapNasaImageList(input: List<NasaImageResponse>?): List<NasaImageModel> {
        return input?.map { mapNasaImage(it) } ?: emptyList()
    }
}

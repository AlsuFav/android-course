package ru.fav.starlight.search.ui.state

import androidx.compose.runtime.Immutable
import ru.fav.starlight.domain.model.NasaImageModel

@Immutable
data class SearchState(
    val startDate: String = "",
    val endDate: String = "",
    val isLoading: Boolean = false,
    val nasaImages: List<NasaImageModel> = emptyList(),
    val fieldError: String = "",
    val globalError: String = "",
)

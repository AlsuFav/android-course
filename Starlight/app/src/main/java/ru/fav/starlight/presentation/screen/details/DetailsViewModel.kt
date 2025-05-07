package ru.fav.starlight.presentation.screen.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.fav.starlight.R
import ru.fav.starlight.domain.exception.ForbiddenAccessException
import ru.fav.starlight.domain.exception.NetworkException
import ru.fav.starlight.domain.exception.ServerException
import ru.fav.starlight.domain.provider.ResourceProvider
import ru.fav.starlight.domain.usecase.GetNasaImageDetailsUseCase
import ru.fav.starlight.presentation.navigation.NavMain
import ru.fav.starlight.presentation.screen.details.state.DetailsEvent
import ru.fav.starlight.presentation.screen.details.state.NasaImageDetailsState
import ru.fav.starlight.presentation.screen.profile.state.ProfileEvent
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val getNasaImageDetailsUseCase: GetNasaImageDetailsUseCase,
    private val resourceProvider: ResourceProvider,
    private val navMain: NavMain,
) : ViewModel() {

    private val _nasaImageDetailsState = MutableStateFlow<NasaImageDetailsState>(
        NasaImageDetailsState.Loading)
    val nasaImageDetailsState = _nasaImageDetailsState.asStateFlow()

    fun reduce(event: DetailsEvent) {
        when (event) {
            is DetailsEvent.GetNasaImageDetails -> loadNasaImageDetails(event.date)
            is DetailsEvent.OnBackClicked -> navigateBack()
        }
    }

    fun loadNasaImageDetails(date: String) {
        viewModelScope.launch {
            runCatching {
                getNasaImageDetailsUseCase(date)
            }.fold(
                onSuccess = { nasaImage -> _nasaImageDetailsState.value = NasaImageDetailsState.Success(nasaImage) },
                onFailure = { e -> _nasaImageDetailsState.value = handleError(e) }
            )
        }
    }

    private fun handleError(throwable: Throwable): NasaImageDetailsState.Error {
        val errorMessage = when (throwable) {
            is ForbiddenAccessException ->
                throwable.message ?: resourceProvider.getString(R.string.error_forbidden_access)
            is NetworkException -> resourceProvider.getString(R.string.error_network)
            is ServerException -> throwable.message ?: resourceProvider.getString(R.string.error_server)
            else -> resourceProvider.getString(R.string.error_unknown)
        }
        return NasaImageDetailsState.Error(errorMessage)
    }

    private fun navigateBack() {
        navMain.goBack()
    }

    override fun onCleared() {
        super.onCleared()
    }
}

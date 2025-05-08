package ru.fav.starlight.presentation.screen.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.fav.starlight.R
import ru.fav.starlight.domain.cache.NasaImageDetailsCacheManager
import ru.fav.starlight.domain.exception.ForbiddenAccessException
import ru.fav.starlight.domain.exception.NetworkException
import ru.fav.starlight.domain.exception.ServerException
import ru.fav.starlight.domain.provider.ResourceProvider
import ru.fav.starlight.domain.usecase.GetNasaImageDetailsUseCase
import ru.fav.starlight.presentation.navigation.NavMain
import ru.fav.starlight.presentation.screen.details.state.DetailsEffect
import ru.fav.starlight.presentation.screen.details.state.DetailsEvent
import ru.fav.starlight.presentation.screen.details.state.NasaImageDetailsState
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val getNasaImageDetailsUseCase: GetNasaImageDetailsUseCase,
    private val resourceProvider: ResourceProvider,
    private val cacheManager: NasaImageDetailsCacheManager,
    private val navMain: NavMain,
) : ViewModel() {

    private val _nasaImageDetailsState = MutableStateFlow<NasaImageDetailsState>(
        NasaImageDetailsState.Loading)
    val nasaImageDetailsState = _nasaImageDetailsState.asStateFlow()

    private val _effect = MutableSharedFlow<DetailsEffect>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effect = _effect.asSharedFlow()

    fun reduce(event: DetailsEvent) {
        when (event) {
            is DetailsEvent.GetNasaImageDetails -> loadNasaImageDetails(event.date)
            is DetailsEvent.OnBackClicked -> navigateBack()
        }
    }

    private fun loadNasaImageDetails(date: String) {
        val cachedData = cacheManager.get(date)
        if (cachedData != null) {
            _nasaImageDetailsState.value = NasaImageDetailsState.Success(cachedData)
            viewModelScope.launch {
                _effect.emit(DetailsEffect.ShowToast(resourceProvider.getString(R.string.from_cache)))
            }
            return
        }

        viewModelScope.launch {
            _effect.emit(DetailsEffect.ShowToast(resourceProvider.getString(R.string.from_api)))
            runCatching {
                getNasaImageDetailsUseCase(date)
            }.fold(
                onSuccess = {
                    nasaImage -> _nasaImageDetailsState.value = NasaImageDetailsState.Success(nasaImage)
                    cacheManager.put(date, nasaImage)
                },
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

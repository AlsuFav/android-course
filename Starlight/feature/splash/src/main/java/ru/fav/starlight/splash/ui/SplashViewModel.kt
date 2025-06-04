package ru.fav.starlight.splash.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.fav.starlight.presentation.R
import ru.fav.starlight.domain.exception.NoApiKeyException
import ru.fav.starlight.domain.provider.ResourceProvider
import ru.fav.starlight.domain.usecase.FetchRemoteConfigUseCase
import ru.fav.starlight.domain.usecase.GetApiKeyUseCase
import ru.fav.starlight.navigation.NavMain
import ru.fav.starlight.splash.ui.state.SplashEffect
import ru.fav.starlight.splash.ui.state.SplashEvent
import ru.fav.starlight.splash.ui.state.SplashState
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getApiKeyUseCase: GetApiKeyUseCase,
    private val fetchRemoteConfigUseCase: FetchRemoteConfigUseCase,
    private val resourceProvider: ResourceProvider,
    private val navMain: NavMain,
) : ViewModel() {

    init {
        viewModelScope.launch {
            fetchRemoteConfigUseCase()
        }
    }

    private val _splashState = MutableStateFlow<SplashState>(SplashState.Loading)
    val splashState = _splashState.asStateFlow()

    private val _effect = MutableSharedFlow<SplashEffect>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effect = _effect.asSharedFlow()

    fun reduce(event: SplashEvent) {
        when (event) {
            is SplashEvent.CheckApiKey -> getApiKey()
        }
    }

    private fun getApiKey() {
        viewModelScope.launch {
            runCatching {
                getApiKeyUseCase()
            }.fold(
                onSuccess = {
                    _splashState.value = SplashState.Success
                    navigateToSearch()
                },
                onFailure = { e -> _splashState.value = handleError(e) }
            )
        }
    }


    private suspend fun handleError(throwable: Throwable): SplashState.Error {
        return when (throwable) {
            is NoApiKeyException -> {
                navigateToAuthorization()
                SplashState.Error.NoApiKey
            }

            else -> {
                _effect.emit(SplashEffect.ShowErrorDialog(
                    resourceProvider.getString(R.string.error_unknown)
                ))
                SplashState.Error.GlobalError

            }
        }
    }

    private fun navigateToSearch() {
        navMain.goToSearchPage()
    }

    private fun navigateToAuthorization() {
        navMain.goToAuthorizationPage()
    }

    override fun onCleared() {
        super.onCleared()
    }
}

package ru.fav.starlight.presentation.screen.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.fav.starlight.R
import ru.fav.starlight.domain.exception.NoApiKeyException
import ru.fav.starlight.domain.provider.ResourceProvider
import ru.fav.starlight.domain.usecase.GetApiKeyUseCase
import ru.fav.starlight.presentation.navigation.NavMain
import ru.fav.starlight.presentation.screen.authorization.state.AuthorizationEvent
import ru.fav.starlight.presentation.screen.splash.state.SplashEvent
import ru.fav.starlight.presentation.screen.splash.state.SplashState
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getApiKeyUseCase: GetApiKeyUseCase,
    private val resourceProvider: ResourceProvider,
    private val navMain: NavMain,
) : ViewModel() {

    private val _splashState = MutableStateFlow<SplashState>(SplashState.Loading)
    val splashState = _splashState.asStateFlow()

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
                    navigateToProfile()
                },
                onFailure = { e -> _splashState.value = handleError(e) }
            )
        }
    }


    private fun handleError(throwable: Throwable): SplashState.Error {
        return when (throwable) {
            is NoApiKeyException -> {
                navigateToAuthorization()
                SplashState.Error.NoApiKey
            }

            else ->
                SplashState.Error.GlobalError(resourceProvider.getString(R.string.error_unknown))
        }
    }

    private fun navigateToProfile() {
        navMain.goToProfilePage()
    }

    private fun navigateToAuthorization() {
        navMain.goToAuthorizationPage()
    }

    override fun onCleared() {
        super.onCleared()
    }
}

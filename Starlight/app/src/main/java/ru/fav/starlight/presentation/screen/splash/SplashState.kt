package ru.fav.starlight.presentation.screen.splash

sealed class SplashState {
    object Loading : SplashState()
    object Success : SplashState()

    sealed class Error : SplashState() {
        object NoApiKey : Error()
        data class GlobalError(val message: String) : Error()
    }
}

package ru.fav.starlight.splash.ui.state

sealed class SplashEffect {
    data class ShowErrorDialog(val message: String) : SplashEffect()
}

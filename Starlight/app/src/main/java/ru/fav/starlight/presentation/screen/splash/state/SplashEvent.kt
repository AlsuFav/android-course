package ru.fav.starlight.presentation.screen.splash.state

sealed class SplashEvent {
    object CheckApiKey : SplashEvent()
}
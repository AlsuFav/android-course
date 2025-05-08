package ru.fav.starlight.presentation.screen.authorization.state

sealed class AuthorizationEvent {
    data class OnSignInClicked(val apiKey: String) : AuthorizationEvent()
}


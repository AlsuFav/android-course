package ru.fav.starlight.authorization.ui.state

sealed class AuthorizationEvent {
    data class OnSignInClicked(val apiKey: String) : AuthorizationEvent()
}


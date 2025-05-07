package ru.fav.starlight.presentation.screen.profile.state

sealed class ProfileEvent {
    object OnLogOutClicked : ProfileEvent()
    data class OnUpdateApiKeyClicked(val apiKey: String) : ProfileEvent()
}
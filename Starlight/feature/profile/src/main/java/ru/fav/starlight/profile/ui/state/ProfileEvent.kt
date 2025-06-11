package ru.fav.starlight.profile.ui.state

sealed class ProfileEvent {
    object OnLogOutClicked : ProfileEvent()
    data class OnUpdateApiKeyClicked(val apiKey: String) : ProfileEvent()
}

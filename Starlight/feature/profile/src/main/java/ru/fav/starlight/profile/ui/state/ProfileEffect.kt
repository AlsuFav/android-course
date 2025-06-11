package ru.fav.starlight.profile.ui.state

sealed class ProfileEffect {
    data class ShowToast(val message: String) : ProfileEffect()
    data class ShowErrorDialog(val message: String) : ProfileEffect()
}

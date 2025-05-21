package ru.fav.starlight.authorization.ui.state

sealed class AuthorizationEffect {
    data class ShowErrorDialog(val message: String) : AuthorizationEffect()
}

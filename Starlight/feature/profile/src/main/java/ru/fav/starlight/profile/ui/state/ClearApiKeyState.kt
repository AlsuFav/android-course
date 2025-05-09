package ru.fav.starlight.profile.ui.state

sealed class ClearApiKeyState {
    object Initial : ClearApiKeyState()
    object Loading : ClearApiKeyState()
    object Success : ClearApiKeyState()

    data class Error(val message: String) : ClearApiKeyState()
}

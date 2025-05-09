package ru.fav.starlight.profile.ui.state

sealed class UpdateApiKeyState {
    object Initial : UpdateApiKeyState()
    object Loading : UpdateApiKeyState()
    object Success : UpdateApiKeyState()

    sealed class Error : UpdateApiKeyState() {
        data class FieldError(val message: String) : Error()
        data class GlobalError(val message: String) : Error()
    }
}

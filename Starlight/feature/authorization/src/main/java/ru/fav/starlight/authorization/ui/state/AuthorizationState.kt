package ru.fav.starlight.authorization.ui.state

sealed class AuthorizationState {
    object Initial : AuthorizationState()
    object Loading : AuthorizationState()
    object Success : AuthorizationState()

    sealed class Error : AuthorizationState() {
        data class FieldError(val message: String) : Error()
        object GlobalError : Error()
    }
}

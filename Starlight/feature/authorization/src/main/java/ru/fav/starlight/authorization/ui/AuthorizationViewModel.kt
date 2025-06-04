package ru.fav.starlight.authorization.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.fav.starlight.authorization.ui.state.AuthorizationEffect
import ru.fav.starlight.authorization.ui.state.AuthorizationEvent
import ru.fav.starlight.authorization.ui.state.AuthorizationState
import ru.fav.starlight.domain.provider.ResourceProvider
import ru.fav.starlight.domain.usecase.SaveApiKeyUseCase
import ru.fav.starlight.navigation.NavMain
import ru.fav.starlight.presentation.R
import javax.inject.Inject

@HiltViewModel
class AuthorizationViewModel @Inject constructor(
    private val saveApiKeyUseCase: SaveApiKeyUseCase,
    private val resourceProvider: ResourceProvider,
    private val navMain: NavMain,
) : ViewModel() {

    private val _authorizationState = MutableStateFlow<AuthorizationState>(AuthorizationState.Initial)
    val authorizationState = _authorizationState.asStateFlow()

    private val _effect = MutableSharedFlow<AuthorizationEffect>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effect = _effect.asSharedFlow()

    fun reduce(event: AuthorizationEvent) {
        when (event) {
            is AuthorizationEvent.OnSignInClicked -> authorize(event.apiKey)
        }
    }

    private fun authorize(apiKey: String) {
        validateInput(apiKey)?.let { errorMessage ->
            _authorizationState.value = AuthorizationState.Error.FieldError(errorMessage)
            return
        }

        _authorizationState.value = AuthorizationState.Loading

        viewModelScope.launch {
            runCatching {
                saveApiKeyUseCase(apiKey)
            }.fold(
                onSuccess = {
                    _authorizationState.value = AuthorizationState.Success
                    navigateToProfile()
                },
                onFailure = { _authorizationState.value = handleError() }
            )
        }
    }

    private fun validateInput(apiKey: String): String? {
        return when {
            apiKey.isEmpty() -> resourceProvider.getString(R.string.error_fill_field)
            else -> null
        }
    }

    private suspend fun handleError(): AuthorizationState.Error {
        _effect.emit(AuthorizationEffect.ShowErrorDialog(resourceProvider.getString(R.string.error_unknown)))
        return AuthorizationState.Error.GlobalError
    }

    private fun navigateToProfile() {
        navMain.goToProfilePage()
    }

    override fun onCleared() {
        super.onCleared()
    }
}

package ru.fav.starlight.presentation.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.fav.starlight.R
import ru.fav.starlight.domain.provider.ResourceProvider
import ru.fav.starlight.domain.usecase.ClearApiKeyUseCase
import ru.fav.starlight.domain.usecase.SaveApiKeyUseCase
import ru.fav.starlight.presentation.navigation.NavMain
import ru.fav.starlight.presentation.screen.profile.state.ClearApiKeyState
import ru.fav.starlight.presentation.screen.profile.state.ProfileEvent
import ru.fav.starlight.presentation.screen.profile.state.UpdateApiKeyState
import ru.fav.starlight.presentation.screen.splash.state.SplashEvent
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val saveApiKeyUseCase: SaveApiKeyUseCase,
    private val clearApiKeyUseCase: ClearApiKeyUseCase,
    private val resourceProvider: ResourceProvider,
    private val navMain: NavMain,
) : ViewModel() {

    private val _updateApiKeyState = MutableStateFlow<UpdateApiKeyState>(UpdateApiKeyState.Initial)
    val updateApiKeyState = _updateApiKeyState.asStateFlow()

    private val _clearApiKeyState = MutableStateFlow<ClearApiKeyState>(ClearApiKeyState.Initial)
    val clearApiKeyState = _clearApiKeyState.asStateFlow()

    fun reduce(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.OnUpdateApiKeyClicked -> updateApiKey(event.apiKey)
            is ProfileEvent.OnLogOutClicked -> deleteApiKey()
        }
    }

    fun updateApiKey(apiKey: String) {
        validateInput(apiKey)?.let { errorMessage ->
            _updateApiKeyState.value = UpdateApiKeyState.Error.FieldError(errorMessage)
            return
        }

        _updateApiKeyState.value = UpdateApiKeyState.Loading

        viewModelScope.launch {
            runCatching {
                saveApiKeyUseCase(apiKey)
            }.fold(
                onSuccess = { _updateApiKeyState.value = UpdateApiKeyState.Success },
                onFailure = { _updateApiKeyState.value =
                    UpdateApiKeyState.Error.GlobalError(resourceProvider.getString(R.string.error_unknown)) }
            )
        }
    }

    fun deleteApiKey() {
        _clearApiKeyState.value = ClearApiKeyState.Loading

        viewModelScope.launch {
            runCatching {
                clearApiKeyUseCase()
            }.fold(
                onSuccess = {
                    _clearApiKeyState.value = ClearApiKeyState.Success
                    navigateToAuthorization()
                },
                onFailure = { _clearApiKeyState.value =
                    ClearApiKeyState.Error(resourceProvider.getString(R.string.error_unknown)) }
            )
        }
    }

    private fun validateInput(apiKey: String): String? {
        return when {
            apiKey.isEmpty() -> resourceProvider.getString(R.string.error_fill_field)
            else -> null
        }
    }

    private fun navigateToAuthorization() {
        navMain.goToAuthorizationPage()
    }

    override fun onCleared() {
        super.onCleared()
    }
}

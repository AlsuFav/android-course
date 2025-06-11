package ru.fav.starlight.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.fav.starlight.profile.R
import ru.fav.starlight.domain.provider.ResourceProvider
import ru.fav.starlight.domain.usecase.ClearApiKeyUseCase
import ru.fav.starlight.domain.usecase.SaveApiKeyUseCase
import ru.fav.starlight.navigation.NavMain
import ru.fav.starlight.profile.ui.state.ClearApiKeyState
import ru.fav.starlight.profile.ui.state.ProfileEffect
import ru.fav.starlight.profile.ui.state.ProfileEvent
import ru.fav.starlight.profile.ui.state.UpdateApiKeyState
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

    private val _effect = MutableSharedFlow<ProfileEffect>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effect = _effect.asSharedFlow()

    fun reduce(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.OnUpdateApiKeyClicked -> updateApiKey(event.apiKey)
            is ProfileEvent.OnLogOutClicked -> deleteApiKey()
        }
    }

    private fun updateApiKey(apiKey: String) {
        validateInput(apiKey)?.let { errorMessage ->
            _updateApiKeyState.value = UpdateApiKeyState.Error.FieldError(errorMessage)
            return
        }

        _updateApiKeyState.value = UpdateApiKeyState.Loading

        viewModelScope.launch {
            runCatching {
                saveApiKeyUseCase(apiKey)
            }.fold(
                onSuccess = {
                    _updateApiKeyState.value = UpdateApiKeyState.Success
                    _effect.emit(ProfileEffect.ShowToast(resourceProvider.getString(R.string.api_key_updated)))
                },
                onFailure = {
                    _effect.emit(ProfileEffect.ShowErrorDialog(
                        resourceProvider.getString(ru.fav.starlight.presentation.R.string.error_unknown)
                    ))
                    _updateApiKeyState.value = UpdateApiKeyState.Error.GlobalError
                }
            )
        }
    }

    private fun deleteApiKey() {
        _clearApiKeyState.value = ClearApiKeyState.Loading

        viewModelScope.launch {
            runCatching {
                clearApiKeyUseCase()
            }.fold(
                onSuccess = {
                    _clearApiKeyState.value = ClearApiKeyState.Success
                    navigateToAuthorization()
                },
                onFailure = {
                    _effect.emit(ProfileEffect.ShowErrorDialog(
                        resourceProvider.getString(ru.fav.starlight.presentation.R.string.error_unknown)
                    ))
                    _clearApiKeyState.value = ClearApiKeyState.Error }
            )
        }
    }

    private fun validateInput(apiKey: String): String? {
        return when {
            apiKey.isEmpty() -> resourceProvider.getString(
                ru.fav.starlight.presentation.R.string.error_fill_field
            )
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

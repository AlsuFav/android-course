package ru.fav.starlight.profile.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.androidbroadcast.vbpd.viewBinding
import ru.fav.starlight.presentation.util.ErrorDialogUtil
import ru.fav.starlight.profile.R
import ru.fav.starlight.profile.databinding.FragmentProfileBinding
import ru.fav.starlight.profile.ui.state.ClearApiKeyState
import ru.fav.starlight.profile.ui.state.ProfileEffect
import ru.fav.starlight.profile.ui.state.ProfileEvent
import ru.fav.starlight.profile.ui.state.UpdateApiKeyState
import ru.fav.starlight.utils.extensions.hideKeyboard
import ru.fav.starlight.utils.extensions.observe
import ru.fav.starlight.utils.extensions.observeNotSuspend
import kotlin.getValue

@AndroidEntryPoint
class ProfileFragment: Fragment(R.layout.fragment_profile) {

    private val viewBinding: FragmentProfileBinding by viewBinding(FragmentProfileBinding::bind)

    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeViewModel()
    }

    private fun initViews() = with(viewBinding) {

        this.buttonUpdateApiKey.setOnClickListener {
            val apiKey = this.editTextApiKey.text.toString().trim()
            profileViewModel.reduce(event = ProfileEvent.OnUpdateApiKeyClicked(apiKey))
            hideKeyboard()
        }

        this.buttonLogOut.setOnClickListener {
            profileViewModel.reduce(event = ProfileEvent.OnLogOutClicked)
        }
    }

    private fun observeViewModel() {
        profileViewModel.effect.observeNotSuspend(viewLifecycleOwner) { state ->
            when (state) {
                is ProfileEffect.ShowToast -> showToast(state.message)
            }
        }

        profileViewModel.updateApiKeyState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UpdateApiKeyState.Initial -> {
                    showUpdateButtonLoading(false)
                    hideFieldError()
                }
                is UpdateApiKeyState.Loading -> {
                    hideFieldError()
                    showUpdateButtonLoading(true)
                }
                is UpdateApiKeyState.Success -> {
                    showUpdateButtonLoading(false)
                    hideFieldError()
                }

                is UpdateApiKeyState.Error.FieldError -> {
                    showUpdateButtonLoading(false)
                    showFieldError(state.message)
                }
                is UpdateApiKeyState.Error.GlobalError -> {
                    showUpdateButtonLoading(false)

                    ErrorDialogUtil.showErrorDialog(
                        context = requireContext(),
                        message = state.message
                    )
                }
            }
        }

        profileViewModel.clearApiKeyState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ClearApiKeyState.Initial -> {
                    showLogOutButtonLoading(false)
                    hideFieldError()
                }
                is ClearApiKeyState.Loading -> {
                    hideFieldError()
                    showLogOutButtonLoading(true)
                }
                is ClearApiKeyState.Success -> {
                    showLogOutButtonLoading(false)
                    hideFieldError()
                }

                is ClearApiKeyState.Error -> {
                    showLogOutButtonLoading(false)

                    ErrorDialogUtil.showErrorDialog(
                        context = requireContext(),
                        message = state.message
                    )
                }
            }
        }
    }

    private fun showUpdateButtonLoading(isLoading: Boolean) {
        viewBinding.buttonUpdateApiKey.isEnabled = !isLoading
    }

    private fun showLogOutButtonLoading(isLoading: Boolean) {
        viewBinding.buttonLogOut.isEnabled = !isLoading
    }

    private fun showFieldError(message: String) {
        viewBinding.textError.apply {
            text = message
            visibility = View.VISIBLE
        }
    }

    private fun hideFieldError() {
        viewBinding.textError.visibility = View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}

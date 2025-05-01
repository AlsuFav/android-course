package ru.fav.starlight.presentation.screen.profile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.fav.starlight.R
import ru.fav.starlight.databinding.FragmentProfileBinding
import ru.fav.starlight.presentation.util.ErrorDialogUtil
import ru.fav.starlight.util.hideKeyboard
import ru.fav.starlight.util.observe
import kotlin.getValue

@AndroidEntryPoint
class ProfileFragment: Fragment(R.layout.fragment_profile) {

    private var viewBinding: FragmentProfileBinding? = null

    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = FragmentProfileBinding.bind(view)
        initViews()
        observeViewModel()
    }

    private fun initViews() = with(viewBinding) {

        this?.buttonUpdateApiKey?.setOnClickListener {
            val apiKey = this.editTextApiKey.text.toString().trim()
            profileViewModel.updateApiKey(apiKey)
            hideKeyboard()
        }

        this?.buttonLogOut?.setOnClickListener {
            profileViewModel.deleteApiKey()
        }
    }

    private fun observeViewModel() {
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

                    showToast(getString(R.string.api_key_updated))
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
                    navigateToAuthorizationFragment()
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
        viewBinding?.buttonUpdateApiKey?.isEnabled = !isLoading
    }

    private fun showLogOutButtonLoading(isLoading: Boolean) {
        viewBinding?.buttonLogOut?.isEnabled = !isLoading
    }

    private fun showFieldError(message: String) {
        viewBinding?.textError?.apply {
            text = message
            visibility = View.VISIBLE
        }
    }

    private fun hideFieldError() {
        viewBinding?.textError?.visibility = View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }

    private fun navigateToAuthorizationFragment() {
        findNavController().navigate(R.id.action_profile_to_authorization)
    }
}

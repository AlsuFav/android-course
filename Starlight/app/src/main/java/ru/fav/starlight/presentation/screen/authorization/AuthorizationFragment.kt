package ru.fav.starlight.presentation.screen.authorization

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.fav.starlight.R
import ru.fav.starlight.databinding.FragmentAuthorizationBinding
import ru.fav.starlight.presentation.util.ErrorDialogUtil
import ru.fav.starlight.util.observe

@AndroidEntryPoint
class AuthorizationFragment : Fragment(R.layout.fragment_authorization) {

    private var viewBinding: FragmentAuthorizationBinding? = null

    private val authorizationViewModel: AuthorizationViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = FragmentAuthorizationBinding.bind(view)
        initViews()
        observeViewModel()
    }

    private fun initViews() = with(viewBinding) {

        this?.buttonSignIn?.setOnClickListener {
            val apiKey = this.editTextApiKey.text.toString().trim()
            authorizationViewModel.authorize(apiKey)
        }
    }

    private fun observeViewModel() {
        authorizationViewModel.authorizationState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthorizationState.Initial -> {
                    showLoading(false)
                    hideFieldError()
                }
                is AuthorizationState.Loading -> {
                    hideFieldError()
                    showLoading(true)
                }
                is AuthorizationState.Success -> {
                    showLoading(false)
                    hideFieldError()
                    navigateToProfileFragment()
                }

                is AuthorizationState.Error.FieldError -> {
                    showLoading(false)
                    showFieldError(state.message)
                }
                is AuthorizationState.Error.GlobalError -> {
                    showLoading(false)

                    ErrorDialogUtil.showErrorDialog(
                        context = requireContext(),
                        message = state.message
                    )
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        viewBinding?.buttonSignIn?.isEnabled = !isLoading
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

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }

    private fun navigateToProfileFragment() {
        findNavController().navigate(R.id.action_authorization_to_search)
    }
}

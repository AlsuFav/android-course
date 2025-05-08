package ru.fav.starlight.presentation.screen.authorization

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.androidbroadcast.vbpd.viewBinding
import ru.fav.starlight.R
import ru.fav.starlight.databinding.FragmentAuthorizationBinding
import ru.fav.starlight.presentation.screen.authorization.state.AuthorizationEvent
import ru.fav.starlight.presentation.screen.authorization.state.AuthorizationState
import ru.fav.starlight.presentation.util.ErrorDialogUtil
import ru.fav.starlight.presentation.util.observe

@AndroidEntryPoint
class AuthorizationFragment : Fragment(R.layout.fragment_authorization) {

    private val viewBinding: FragmentAuthorizationBinding by viewBinding(
        FragmentAuthorizationBinding::bind)

    private val authorizationViewModel: AuthorizationViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeViewModel()
    }

    private fun initViews() = with(viewBinding) {

        this.buttonSignIn.setOnClickListener {
            val apiKey = this.editTextApiKey.text.toString().trim()
            authorizationViewModel.reduce(event = AuthorizationEvent.OnSignInClicked(apiKey))
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
        viewBinding.buttonSignIn.isEnabled = !isLoading
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

    override fun onDestroyView() {
        super.onDestroyView()
    }
}

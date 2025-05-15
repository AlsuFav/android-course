package ru.fav.starlight.authorization.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.androidbroadcast.vbpd.viewBinding
import ru.fav.starlight.authorization.ui.state.AuthorizationEvent
import ru.fav.starlight.authorization.ui.state.AuthorizationState
import ru.fav.starlight.authorization.databinding.FragmentAuthorizationBinding
import ru.fav.starlight.authorization.R
import ru.fav.starlight.authorization.ui.state.AuthorizationEffect
import ru.fav.starlight.utils.extensions.observe
import ru.fav.starlight.utils.extensions.observeNotSuspend
import ru.fav.starlight.utils.extensions.showErrorDialog

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
        authorizationViewModel.effect.observeNotSuspend(viewLifecycleOwner) { state ->
            when (state) {
                is AuthorizationEffect.ShowErrorDialog -> showErrorDialog(state.message)
            }
        }

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

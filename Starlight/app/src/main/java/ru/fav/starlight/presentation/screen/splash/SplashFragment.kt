package ru.fav.starlight.presentation.screen.splash

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.fav.starlight.R
import ru.fav.starlight.databinding.FragmentSplashBinding
import ru.fav.starlight.presentation.screen.authorization.state.AuthorizationEvent
import ru.fav.starlight.presentation.screen.splash.state.SplashEvent
import ru.fav.starlight.presentation.screen.splash.state.SplashState
import ru.fav.starlight.presentation.util.ErrorDialogUtil
import ru.fav.starlight.presentation.util.observe

@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {

    private var viewBinding: FragmentSplashBinding? = null

    private val splashViewModel: SplashViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = FragmentSplashBinding.bind(view)
        observeViewModel()
        splashViewModel.reduce(event = SplashEvent.CheckApiKey)
    }

    private fun observeViewModel() {
        splashViewModel.splashState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SplashState.Loading -> {}
                is SplashState.Success -> {}
                is SplashState.Error.NoApiKey -> {}

                is SplashState.Error.GlobalError -> {
                    ErrorDialogUtil.showErrorDialog(
                        context = requireContext(),
                        message = state.message
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }
}

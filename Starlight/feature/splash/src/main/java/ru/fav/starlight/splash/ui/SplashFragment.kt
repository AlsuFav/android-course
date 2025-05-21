package ru.fav.starlight.splash.ui

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import dev.androidbroadcast.vbpd.viewBinding
import ru.fav.starlight.splash.R
import ru.fav.starlight.splash.databinding.FragmentSplashBinding
import ru.fav.starlight.splash.ui.state.SplashEffect
import ru.fav.starlight.splash.ui.state.SplashEvent
import ru.fav.starlight.splash.ui.state.SplashState
import ru.fav.starlight.utils.extensions.observe
import ru.fav.starlight.utils.extensions.observeNotSuspend
import ru.fav.starlight.utils.extensions.showErrorDialog

@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {

    private val viewBinding: FragmentSplashBinding by viewBinding(FragmentSplashBinding::bind)

    private val splashViewModel: SplashViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()
        splashViewModel.reduce(event = SplashEvent.CheckApiKey)
    }

    private fun observeViewModel() {
        splashViewModel.effect.observeNotSuspend(viewLifecycleOwner) { state ->
            when (state) {
                is SplashEffect.ShowErrorDialog -> showErrorDialog(state.message)
            }
        }

        splashViewModel.splashState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SplashState.Loading -> {}
                is SplashState.Success -> {}
                is SplashState.Error.NoApiKey -> {}

                is SplashState.Error.GlobalError -> {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}

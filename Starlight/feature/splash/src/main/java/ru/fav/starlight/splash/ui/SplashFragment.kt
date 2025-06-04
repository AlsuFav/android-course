package ru.fav.starlight.splash.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
        requestNotificationPermissionIfNeeded()
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

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}

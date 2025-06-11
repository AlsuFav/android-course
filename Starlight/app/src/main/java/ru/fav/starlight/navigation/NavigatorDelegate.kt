package ru.fav.starlight.navigation

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigatorDelegate @Inject constructor() {

    private var navProvider: WeakReference<Nav.Provider>? = null

    fun setNavProvider(navProvider: Nav.Provider) {
        this.navProvider = WeakReference(navProvider)
    }

    fun clearNavProvider(navProvider: Nav.Provider) {
        if (this.navProvider?.get() == navProvider) {
            this.navProvider = null
        }
    }

    fun navigate(
        @IdRes action: Int,
        args: Bundle? = null,
        options: NavOptions? = null,
        extras: Navigator.Extras? = null
    ) {
        navProvider?.get()?.getNavController()?.navigate(action, args, options, extras)
    }

    fun navigateBack(): Boolean {
        return navProvider?.get()?.getNavController()?.navigateUp() == true
    }
}

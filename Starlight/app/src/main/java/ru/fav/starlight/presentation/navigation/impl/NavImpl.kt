package ru.fav.starlight.presentation.navigation.impl


import ru.fav.starlight.presentation.navigation.Nav
import ru.fav.starlight.presentation.navigation.NavMain
import ru.fav.starlight.presentation.navigation.NavigatorDelegate
import javax.inject.Inject

class NavImpl @Inject constructor(
    private val navigatorDelegate: NavigatorDelegate,
    private val navMain: NavMain,
) : Nav, NavMain by navMain {

    init {
        initNavMain(parent = this)
    }

    override fun setNavProvider(navProvider: Nav.Provider) {
        navigatorDelegate.setNavProvider(navProvider = navProvider)
    }

    override fun clearNavProvider(navProvider: Nav.Provider) {
        navigatorDelegate.clearNavProvider(navProvider = navProvider)
    }
}

package ru.fav.starlight.navigation

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
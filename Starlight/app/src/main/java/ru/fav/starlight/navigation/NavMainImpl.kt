package ru.fav.starlight.navigation

import android.os.Bundle
import ru.fav.starlight.app.R
import javax.inject.Inject

class NavMainImpl @Inject constructor(
    private val navigatorDelegate: NavigatorDelegate,
) : NavMain {

    private var parent: Nav? = null

    override fun initNavMain(parent: Nav) {
        this.parent = parent
    }

    override fun goBack(): Boolean {
        return navigatorDelegate.navigateBack()
    }

    override fun goToSplashPage() {
        navigatorDelegate.navigate(action = R.id.action_global_splash)
    }

    override fun goToAuthorizationPage() {
        navigatorDelegate.navigate(action = R.id.action_global_authorization)
    }

    override fun goToProfilePage() {
        navigatorDelegate.navigate(action = R.id.action_global_profile)
    }

    override fun goToSearchPage() {
        navigatorDelegate.navigate(action = R.id.action_global_search)
    }


    override fun goToDetailsPage(date: String) {
        val args = Bundle().apply {
            putString("date", date)
        }
        navigatorDelegate.navigate(
            action = R.id.action_global_details,
            args = args
        )
    }

    override fun goToGraphPage() {
        navigatorDelegate.navigate(action = R.id.action_global_graph)
    }

    override fun goToDiagramPage() {
        navigatorDelegate.navigate(action = R.id.action_global_diagram)
    }
}

package ru.fav.starlight.navigation

interface NavMain {

    fun initNavMain(parent: Nav)

    fun goBack(): Boolean

    fun goToSplashPage()

    fun goToAuthorizationPage()

    fun goToProfilePage()

    fun goToSearchPage()

    fun goToDetailsPage(date: String)

    fun goToGraphPage()
}

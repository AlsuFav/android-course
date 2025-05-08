package ru.fav.starlight.presentation.navigation

import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController

interface Nav {

    interface Provider {
        fun getNavController(): NavController?
        fun getSupportFragmentManager(): FragmentManager
    }

    fun setNavProvider(navProvider: Provider)

    fun clearNavProvider(navProvider: Provider)
}

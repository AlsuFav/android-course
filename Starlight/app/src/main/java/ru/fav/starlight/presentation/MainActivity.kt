package ru.fav.starlight.presentation

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.fav.starlight.R
import ru.fav.starlight.databinding.ActivityMainBinding


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var viewBinding: ActivityMainBinding? = null
    private var navController: NavController? = null
    private var appBarConfiguration: AppBarConfiguration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding?.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.menu_search_tab, R.id.menu_profile_tab)
        )

        setupSystemWindowInsets()
        setupBottomNavigation()
        setupNavigationListeners()
    }

    private fun setupSystemWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                view.paddingLeft,
                systemBars.top,
                view.paddingRight,
                0
            )
            insets
        }
    }

    private fun setupBottomNavigation() {

        navController?.let {
            viewBinding?.mainBottomNavigation?.setupWithNavController(it)
        }

        navController?.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id) {
                R.id.splashFragment -> hideBottomNavigation()
                R.id.authorizationFragment -> hideBottomNavigation()
                R.id.detailsFragment -> hideBottomNavigation()
                else -> showBottomNavigation()
            }
        }
    }

    private fun setupNavigationListeners() {
        viewBinding?.mainBottomNavigation?.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_search_tab -> {
                    navController?.navigate(R.id.searchFragment)
                    true
                }
                R.id.menu_profile_tab -> {
                    navController?.navigate(R.id.profileFragment)
                    true
                }
                else -> false
            }
        }
    }

    fun showBottomNavigation() {
        viewBinding?.mainBottomNavigation?.visibility = View.VISIBLE
    }

    fun hideBottomNavigation() {
        viewBinding?.mainBottomNavigation?.visibility = View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController?.navigateUp() == true || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        viewBinding = null
        super.onDestroy()
    }
}

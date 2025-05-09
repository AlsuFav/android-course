package ru.fav.starlight

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.androidbroadcast.vbpd.viewBinding
import ru.fav.starlight.app.R
import ru.fav.starlight.app.databinding.ActivityMainBinding
import ru.fav.starlight.navigation.Nav
import ru.fav.starlight.navigation.NavMain
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), Nav.Provider {

    @Inject
    lateinit var nav: Nav

    private val viewBinding by viewBinding(ActivityMainBinding::bind)
    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupSystemWindowInsets()
        setupNavigation()
        setupBottomNavigation()
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

    private fun setupNavigation() {
        if (navController == null) {
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navController = navHostFragment.navController
        }
        nav.setNavProvider(this)

        navController?.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id) {
                ru.fav.starlight.splash.R.id.destination_splash -> hideBottomNavigation()
                ru.fav.starlight.authorization.R.id.destination_authorization -> hideBottomNavigation()
                ru.fav.starlight.details.R.id.destination_details -> hideBottomNavigation()
                else -> showBottomNavigation()
            }
        }
    }

    private fun setupBottomNavigation() {
        navController?.let {
            viewBinding.mainBottomNavigation.setupWithNavController(it)
        }

        viewBinding.mainBottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_search_tab -> {
                    (nav as? NavMain)?.goToSearchPage()
                    true
                }
                R.id.menu_profile_tab -> {
                    (nav as? NavMain)?.goToProfilePage()
                    true
                }
                else -> false
            }
        }
    }

    private fun showBottomNavigation() {
        viewBinding.mainBottomNavigation.visibility = View.VISIBLE
    }

    private fun hideBottomNavigation() {
        viewBinding.mainBottomNavigation.visibility = View.GONE
    }

    override fun getNavController(): NavController? {
        return navController
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::nav.isInitialized) {
            nav.clearNavProvider(navProvider = this)
        }
    }
}

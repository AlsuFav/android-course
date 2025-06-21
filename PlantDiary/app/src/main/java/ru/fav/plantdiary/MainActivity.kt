package ru.fav.plantdiary

import android.content.Context
import android.os.Bundle
import ru.fav.plantdiary.base.BaseActivity
import ru.fav.plantdiary.databinding.ActivityMainBinding
import ru.fav.plantdiary.fragments.AuthorizationFragment
import ru.fav.plantdiary.fragments.PlantsFragment
import ru.fav.plantdiary.utils.Constants
import ru.fav.plantdiary.utils.NavigationAction
import ru.fav.plantdiary.utils.PermissionsHandler


class MainActivity : BaseActivity() {

    override val mainContainerId = R.id.main_fragment_container
    private var viewBinding: ActivityMainBinding? = null
    var permissionsHandler: PermissionsHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding?.root)

        permissionsHandler = PermissionsHandler(
            onSinglePermissionGranted = {},
            onSinglePermissionDenied = {}
        )
        permissionsHandler?.initContracts(this)

        if (isUserLoggedIn()) {
            val userId = getLoggedInUserId()
            navigateToPlantsFragment(userId)
        } else {
            navigateToAuthorizationFragment()
        }
    }


    override fun onDestroy() {
        viewBinding = null
        permissionsHandler = null
        super.onDestroy()
    }


    fun saveLoginState(context: Context, id: String? = null) {
        val sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString(Constants.KEY_USER_ID, id)
            apply()
        }
    }


    private fun isUserLoggedIn(): Boolean {
        return getLoggedInUserId() != null
    }


    private fun getLoggedInUserId(): String? {
        val sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(Constants.KEY_USER_ID, null)
    }


    private fun navigateToAuthorizationFragment() {
        navigate(
            destination = AuthorizationFragment(),
            destinationTag = AuthorizationFragment.AUTHORIZATION_TAG,
            action = NavigationAction.REPLACE,
            isAddToBackStack = false
        )
    }


    private fun navigateToPlantsFragment(id: String?) {
        if (!id.isNullOrEmpty()) {
            navigate(
                destination = PlantsFragment.getInstance(param = id),
                destinationTag = PlantsFragment.PLANTS_TAG,
                action = NavigationAction.REPLACE,
                isAddToBackStack = false
            )
        } else {
            navigateToAuthorizationFragment()
        }
    }

}


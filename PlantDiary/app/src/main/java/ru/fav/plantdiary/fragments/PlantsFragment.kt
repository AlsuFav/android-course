package ru.fav.plantdiary.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import ru.fav.plantdiary.MainActivity
import ru.fav.plantdiary.R
import ru.fav.plantdiary.adapter.recycler.PlantsAdapter
import ru.fav.plantdiary.data.db.entities.PlantEntity
import ru.fav.plantdiary.databinding.FragmentPlantsBinding
import ru.fav.plantdiary.di.ServiceLocator
import ru.fav.plantdiary.utils.NavigationAction

class PlantsFragment: Fragment(R.layout.fragment_plants) {

    private var viewBinding: FragmentPlantsBinding? = null
    private var plantRepository = ServiceLocator.getPlantRepository()
    private var rvAdapter: PlantsAdapter? = null
    private var userId: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = FragmentPlantsBinding.bind(view)

        setupRecyclerView()
        userId = arguments?.getString(ID)

        if (userId.isNullOrEmpty()) {
            handleUnknownUser()
            return
        }

        loadPlants()
        setupLogoutButton()
        viewBinding?.fabAddPlant?.setOnClickListener {
            userId?.let { safeUserId ->
                (requireActivity() as? MainActivity)?.navigate(
                    destination = AddPlantFragment.getInstance(safeUserId),
                    destinationTag = AddPlantFragment.ADD_PLANT_TAG,
                    action = NavigationAction.REPLACE,
                    isAddToBackStack = false
                )
            }
        }
    }

    private fun setupRecyclerView() {
        rvAdapter = PlantsAdapter()
        viewBinding?.recyclerViewPlants?.adapter = rvAdapter

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val plant = rvAdapter?.getItem(position)

                plant?.let {
                    showDeleteDialog(it, position)
                }
            }
        })

        itemTouchHelper.attachToRecyclerView(viewBinding?.recyclerViewPlants)
    }

    private fun handleUnknownUser() {
        showToast(getString(R.string.error_unknown_user))
        navigateToAuthorization()
    }

    private fun navigateToAuthorization() {
        (requireActivity() as? MainActivity)?.navigate(
            destination = AuthorizationFragment(),
            destinationTag = AuthorizationFragment.AUTHORIZATION_TAG,
            action = NavigationAction.REPLACE,
            isAddToBackStack = true
        )
    }

    private fun setupLogoutButton() {
        viewBinding?.buttonLogout?.setOnClickListener {
            (requireActivity() as? MainActivity)?.saveLoginState(requireContext(), id = null)
            navigateToAuthorization()
        }
    }


    private fun loadPlants() {
        lifecycleScope.launch {
            val plants = userId?.let { plantRepository.getPlantsByUserId(it) }

            if (plants != null) {
                rvAdapter?.updateData(plants.toMutableList())
            }
        }
    }

    private fun showDeleteDialog(plant: PlantEntity, position: Int) {
        AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.alert_delete_message))
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                lifecycleScope.launch {
                    plantRepository.deletePlant(plant)
                    rvAdapter?.removeItem(plant)?.let {
                        rvAdapter?.notifyItemRemoved(it)
                    }
                }
            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                rvAdapter?.notifyItemChanged(position)
            }
            .create()
            .show()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewBinding = null
    }

    companion object {
        const val PLANTS_TAG = "PLANTS_TAG"
        private const val ID = "ID"

        fun getInstance(
            param: String
        ):PlantsFragment {
            return PlantsFragment().apply {
                arguments = bundleOf(ID to param)
            }
        }
    }
}
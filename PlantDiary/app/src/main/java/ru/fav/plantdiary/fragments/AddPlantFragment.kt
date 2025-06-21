package ru.fav.plantdiary.fragments

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import ru.fav.plantdiary.MainActivity
import ru.fav.plantdiary.R
import ru.fav.plantdiary.data.db.converters.PhotoConverter
import ru.fav.plantdiary.data.db.entities.PlantEntity
import ru.fav.plantdiary.databinding.FragmentAddPlantBinding
import ru.fav.plantdiary.di.ServiceLocator
import ru.fav.plantdiary.utils.NavigationAction
import ru.fav.plantdiary.utils.validators.DateValidator

class AddPlantFragment(): BottomSheetDialogFragment(R.layout.fragment_add_plant) {

    private var viewBinding: FragmentAddPlantBinding? = null
    private val plantRepository = ServiceLocator.getPlantRepository()
    private var photoUri: Uri? = null
    private var userId: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = FragmentAddPlantBinding.bind(view)

        userId = arguments?.getString(ID)

        if (userId.isNullOrEmpty()) {
            handleUnknownUser()
            return
        }

        viewBinding?.buttonSelectPhoto?.setOnClickListener {
            givePermission()
        }
        viewBinding?.buttonSavePlant?.setOnClickListener {
            savePlant()
        }
    }

    private fun savePlant() {
        val plantName = viewBinding?.editTextPlantName?.text.toString()
        val plantPlantingYearText = viewBinding?.editTextPlantPlantingYear?.text.toString().trim()
        val plantPlantingYear = if (plantPlantingYearText.isEmpty()) 0 else plantPlantingYearText.toInt()

        if (plantName.isEmpty()) {
            viewBinding?.editTextPlantName?.error = getString(R.string.enter_plant_name)
            return
        }

        if (plantPlantingYear < 1) {
            showToast(getString(R.string.invalid_year))
            return
        }

        if (DateValidator().isYearInFuture(plantPlantingYear)) {
            showToast(getString(R.string.year_in_future))
            return
        }

        val photoByteArray = photoUri?.let { uri ->
            requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)?.let { bitmap ->
                    PhotoConverter().toByteArray(bitmap)
                }
            }
        }

        lifecycleScope.launch {
            val plant = PlantEntity(
                userId = userId ?: "",
                name = plantName,
                plantingYear = plantPlantingYear,
                photo = photoByteArray
            )
            plantRepository.savePlant(plant)
            userId?.let { safeUserId ->
                (requireActivity() as? MainActivity)?.navigate(
                    destination = PlantsFragment.getInstance(safeUserId),
                    destinationTag = PlantsFragment.PLANTS_TAG,
                    action = NavigationAction.REPLACE,
                    isAddToBackStack = false
                )
            }
        }
    }

    private fun givePermission() {
        if (requireContext().checkSelfPermission(android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            (requireActivity() as? MainActivity)?.permissionsHandler?.requestSinglePermission(
                permission = android.Manifest.permission.READ_MEDIA_IMAGES,
                onGranted = { openGallery() },
                onDenied = { showToast(getString(R.string.permission_denied)) }
            )
        }else {
            openGallery()
        }
    }


    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    @SuppressLint("CheckResult")
    private fun loadImage(imageUri: Uri) {
        viewBinding?.imageViewPlantPhoto?.let {
            Glide.with(this)
                .load(imageUri)
                .into(it)
        }
    }


    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                photoUri = uri
                loadImage(uri)
            }
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

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewBinding = null
    }

    companion object {
        const val ADD_PLANT_TAG = "ADD_PLANT_TAG"

        private const val ID = "ID"

        fun getInstance(id: String): AddPlantFragment {
            return AddPlantFragment().apply {
                arguments = bundleOf(ID to id)
            }
        }
    }
}
package ru.fav.starlight.presentation.screen.search

import android.app.DatePickerDialog
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.fav.starlight.R
import ru.fav.starlight.databinding.FragmentSearchBinding
import ru.fav.starlight.presentation.adapter.NasaImagesAdapter
import ru.fav.starlight.presentation.adapter.NasaImagesShimmerAdapter
import ru.fav.starlight.presentation.util.ErrorDialogUtil
import ru.fav.starlight.util.observe
import ru.fav.starlight.util.observeNotSuspend
import java.util.Calendar

@AndroidEntryPoint
class SearchFragment: Fragment(R.layout.fragment_search) {

    private var viewBinding: FragmentSearchBinding? = null
    private var rvAdapter: NasaImagesAdapter? = null
    private var rvShimmerAdapter: NasaImagesShimmerAdapter? = null

    private val searchViewModel: SearchViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = FragmentSearchBinding.bind(view)

        initViews()
        observeViewModel()
    }

    private fun initViews() = with(viewBinding) {
        setupRecyclerViews()

        this?.editTextStartDate?.setOnClickListener { showDatePicker(DateType.START) }
        this?.editTextEndDate?.setOnClickListener { showDatePicker(DateType.END) }

        this?.buttonFetchImages?.setOnClickListener {
            val startDate = this.editTextStartDate.text.toString()
            val endDate = this.editTextEndDate.text.toString()
            searchViewModel.loadNasaImages(startDate, endDate)
        }

    }

    private fun observeViewModel() {
        searchViewModel.searchDatesState.observeNotSuspend(viewLifecycleOwner) { state ->
            viewBinding?.apply {
                editTextStartDate.setText(state.startDate)
                editTextEndDate.setText(state.endDate)
            }
        }

        searchViewModel.nasaImagesState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is NasaImagesState.Initial -> {
                    showLoading(false)
                    hideErrorField()
                }
                is NasaImagesState.Loading -> {
                    hideErrorField()
                    showLoading(true)
                }
                is NasaImagesState.Success -> {
                    showLoading(false)
                    hideErrorField()
                    rvAdapter?.updateData(state.nasaImages.toMutableList())
                }

                is NasaImagesState.Error.FieldError -> {
                    showLoading(false)
                    showErrorField(state.message)
                }
                is NasaImagesState.Error.GlobalError -> {
                    showLoading(false)

                    ErrorDialogUtil.showErrorDialog(
                        context = requireContext(),
                        message = state.message
                    )
                }
            }
        }
    }

    private fun setupRecyclerViews() {
        rvAdapter = NasaImagesAdapter { nasaImage ->
            navigateToDetailsFragment(nasaImage.date)
        }
        viewBinding?.recyclerViewNasaImages?.adapter = rvAdapter

        rvShimmerAdapter = NasaImagesShimmerAdapter()
        viewBinding?.recyclerViewShimmer?.adapter = rvShimmerAdapter
    }

    private fun showDatePicker(dateType: DateType) {
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val selectedDate = Calendar.getInstance().apply { set(year, month, day) }
                searchViewModel.onDateSelected(dateType, selectedDate)
            },
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.maxDate = searchViewModel.getCurrentTimeMillis()
            show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        viewBinding?.buttonFetchImages?.isEnabled = !isLoading

        viewBinding?.apply {
            if (isLoading) {
                shimmerLayout.visibility = View.VISIBLE
                shimmerLayout.startShimmer()
                recyclerViewNasaImages.visibility = View.GONE
            } else {
                shimmerLayout.visibility = View.GONE
                shimmerLayout.stopShimmer()
                recyclerViewNasaImages.visibility = View.VISIBLE
            }
        }
    }

    private fun showErrorField(message: String) {
        viewBinding?.textError?.apply {
            text = message
            visibility = View.VISIBLE
        }
    }

    private fun hideErrorField() {
        viewBinding?.textError?.visibility = View.GONE
    }

    private fun navigateToDetailsFragment(date: String) {
        val action = SearchFragmentDirections.actionSearchToDetails(date)
        findNavController().navigate(action)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding?.shimmerLayout?.stopShimmer()
        viewBinding = null
    }
}

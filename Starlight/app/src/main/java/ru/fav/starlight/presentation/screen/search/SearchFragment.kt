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
import ru.fav.starlight.presentation.screen.search.adapter.NasaImagesAdapter
import ru.fav.starlight.presentation.screen.search.adapter.NasaImagesShimmerAdapter
import ru.fav.starlight.presentation.screen.search.state.DateType
import ru.fav.starlight.presentation.screen.search.state.NasaImagesState
import ru.fav.starlight.presentation.screen.search.state.SearchEffect
import ru.fav.starlight.presentation.screen.search.state.SearchEvent
import ru.fav.starlight.presentation.util.ErrorDialogUtil
import ru.fav.starlight.presentation.util.observe
import ru.fav.starlight.presentation.util.observeNotSuspend
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

        this?.editTextStartDate?.setOnClickListener { searchViewModel.reduce(SearchEvent.OnStartDateClicked) }
        this?.editTextEndDate?.setOnClickListener { searchViewModel.reduce(SearchEvent.OnEndDateClicked) }

        this?.buttonFetchImages?.setOnClickListener {
            val startDate = this.editTextStartDate.text.toString()
            val endDate = this.editTextEndDate.text.toString()

            searchViewModel.reduce(SearchEvent.OnFetchImagesClicked(startDate, endDate))
        }

    }

    private fun observeViewModel() {
        searchViewModel.searchDatesState.observeNotSuspend(viewLifecycleOwner) { state ->
            viewBinding?.apply {
                editTextStartDate.setText(state.startDate)
                editTextEndDate.setText(state.endDate)
            }
        }

        searchViewModel.effect.observeNotSuspend(viewLifecycleOwner) { state ->
            when (state) {
                is SearchEffect.ShowDatePicker -> showDatePicker(state.type, state.maxDateMillis)
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
            searchViewModel.reduce(SearchEvent.OnNasaImageClicked(nasaImage.date))
        }
        viewBinding?.recyclerViewNasaImages?.adapter = rvAdapter

        rvShimmerAdapter = NasaImagesShimmerAdapter()
        viewBinding?.recyclerViewShimmer?.adapter = rvShimmerAdapter
    }

    private fun showDatePicker(dateType: DateType, maxDateMillis: Long) {
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val selectedDate = Calendar.getInstance().apply { set(year, month, day) }
                searchViewModel.reduce(SearchEvent.OnDateSelected(dateType, selectedDate))
            },
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.maxDate = maxDateMillis
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

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding?.shimmerLayout?.stopShimmer()
        viewBinding = null
    }
}

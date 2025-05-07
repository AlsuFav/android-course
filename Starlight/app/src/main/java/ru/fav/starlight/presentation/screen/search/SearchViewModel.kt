package ru.fav.starlight.presentation.screen.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.fav.starlight.R
import ru.fav.starlight.domain.exception.ForbiddenAccessException
import ru.fav.starlight.domain.exception.NetworkException
import ru.fav.starlight.domain.exception.ServerException
import ru.fav.starlight.domain.provider.DateProvider
import ru.fav.starlight.domain.provider.ResourceProvider
import ru.fav.starlight.domain.usecase.GetNasaImagesUseCase
import ru.fav.starlight.presentation.navigation.NavMain
import ru.fav.starlight.presentation.screen.search.state.DateType
import ru.fav.starlight.presentation.screen.search.state.NasaImagesState
import ru.fav.starlight.presentation.screen.search.state.SearchDatesState
import ru.fav.starlight.presentation.screen.search.state.SearchEffect
import ru.fav.starlight.presentation.screen.search.state.SearchEvent
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getNasaImagesUseCase: GetNasaImagesUseCase,
    private val dateProvider: DateProvider,
    private val resourceProvider: ResourceProvider,
    private val navMain: NavMain,
) : ViewModel() {

    private val _searchDatesState = MutableStateFlow(SearchDatesState())
    val searchDatesState: StateFlow<SearchDatesState> = _searchDatesState.asStateFlow()


    private val _nasaImagesState = MutableStateFlow<NasaImagesState>(NasaImagesState.Initial)
    val nasaImagesState = _nasaImagesState.asStateFlow()

    private val _effect = MutableSharedFlow<SearchEffect>()
    val effect = _effect.asSharedFlow()

    init {
        reduce(SearchEvent.LoadInitialData)
    }

    fun reduce(event: SearchEvent) {
        when (event) {
            is SearchEvent.LoadInitialData -> loadInitialDates()
            is SearchEvent.LoadCurrentTimeMillis -> getCurrentTimeMillis()
            is SearchEvent.OnFetchImagesClicked -> loadNasaImages(event.startDate, event.endDate)
            is SearchEvent.OnStartDateClicked -> showDatePicker(DateType.START)
            is SearchEvent.OnEndDateClicked -> showDatePicker(DateType.END)
            is SearchEvent.OnDateSelected -> onDateSelected(event.type, event.calendar)
            is SearchEvent.OnNasaImageClicked -> navigateToDetails(event.date)
        }
    }

    fun loadNasaImages(startDate: String, endDate: String) {
        validateInputs(startDate, endDate)?.let { errorMessage ->
            _nasaImagesState.value = NasaImagesState.Error.FieldError(errorMessage)
            return
        }

        _nasaImagesState.value = NasaImagesState.Loading

        viewModelScope.launch {
            runCatching {
                getNasaImagesUseCase(startDate, endDate)
            }.fold(
                onSuccess = { nasaImages -> _nasaImagesState.value = NasaImagesState.Success(nasaImages) },
                onFailure = { e -> _nasaImagesState.value = handleError(e) }
            )
        }
    }

    private fun validateInputs(startDateString: String, endDateString: String): String? {
        val startDate = dateProvider.parseDate(startDateString)
        val endDate = dateProvider.parseDate(endDateString)
        val currentDate = dateProvider.getCurrentDate()

        return when {
            startDate.after(endDate) -> resourceProvider.getString(R.string.error_start_date_after_end_date)

            startDate.after(currentDate) || endDate.after(currentDate)
                -> resourceProvider.getString(R.string.error_dates_after) + dateProvider.formatDate(currentDate)
            else -> null
        }
    }

    private fun handleError(throwable: Throwable): NasaImagesState.Error.GlobalError {
        val errorMessage = when (throwable) {
            is ForbiddenAccessException ->
                throwable.message ?: resourceProvider.getString(R.string.error_forbidden_access)
            is NetworkException -> resourceProvider.getString(R.string.error_network)
            is ServerException -> throwable.message ?: resourceProvider.getString(R.string.error_server)
            else -> resourceProvider.getString(R.string.error_unknown)
        }
        return NasaImagesState.Error.GlobalError(errorMessage)
    }

    private fun loadInitialDates() {
        val currentDate = dateProvider.getCurrentDate()
        val formattedCurrentDate = dateProvider.formatDate(currentDate)
        _searchDatesState.update {
            it.copy(
                startDate = formattedCurrentDate,
                endDate = formattedCurrentDate
            )
        }
    }

    fun onDateSelected(dateType: DateType, calendar: Calendar) {
        val date = dateProvider.formatDate(calendar)
        _searchDatesState.update { state ->
            when(dateType) {
                DateType.START -> state.copy(startDate = date)
                DateType.END -> state.copy(endDate = date)
            }
        }
    }

    private fun showDatePicker(type: DateType) {
        viewModelScope.launch {
            _effect.emit(SearchEffect.ShowDatePicker(type, dateProvider.getCurrentDate().timeInMillis))
        }
    }

    fun getCurrentTimeMillis(): Long = dateProvider.getCurrentDate().timeInMillis

    private fun navigateToDetails(date: String) {
        navMain.goToDetailsPage(date)
    }

    override fun onCleared() {
        super.onCleared()
    }
}

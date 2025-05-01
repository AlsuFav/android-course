package ru.fav.starlight.presentation.screen.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getNasaImagesUseCase: GetNasaImagesUseCase,
    private val dateProvider: DateProvider,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    private val _searchDatesState = MutableStateFlow(SearchDatesState())
    val searchDatesState: StateFlow<SearchDatesState> = _searchDatesState.asStateFlow()


    private val _nasaImagesState = MutableStateFlow<NasaImagesState>(NasaImagesState.Initial)
    val nasaImagesState = _nasaImagesState.asStateFlow()

    init {
        loadInitialDates()
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

    fun getCurrentTimeMillis(): Long = dateProvider.getCurrentDate().timeInMillis

    override fun onCleared() {
        super.onCleared()
    }
}

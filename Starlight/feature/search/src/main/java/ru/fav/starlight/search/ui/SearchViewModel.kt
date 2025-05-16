package ru.fav.starlight.search.ui

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
import ru.fav.starlight.search.R
import ru.fav.starlight.domain.cache.NasaImagesCacheManager
import ru.fav.starlight.domain.exception.ForbiddenAccessException
import ru.fav.starlight.domain.exception.NetworkException
import ru.fav.starlight.domain.exception.ServerException
import ru.fav.starlight.domain.provider.DateProvider
import ru.fav.starlight.domain.provider.ResourceProvider
import ru.fav.starlight.domain.usecase.GetNasaImagesUseCase
import ru.fav.starlight.navigation.NavMain
import ru.fav.starlight.search.ui.state.DateType
import ru.fav.starlight.search.ui.state.SearchEffect
import ru.fav.starlight.search.ui.state.SearchEvent
import ru.fav.starlight.search.ui.state.SearchState
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getNasaImagesUseCase: GetNasaImagesUseCase,
    private val dateProvider: DateProvider,
    private val resourceProvider: ResourceProvider,
    private val cacheManager: NasaImagesCacheManager,
    private val navMain: NavMain,
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()


    private val _effect = MutableSharedFlow<SearchEffect>()
    val effect = _effect.asSharedFlow()

    init {
        reduce(SearchEvent.LoadInitialData)
    }

    fun reduce(event: SearchEvent) {
        when (event) {
            is SearchEvent.LoadInitialData -> loadInitialDates()
            is SearchEvent.OnFetchImagesClicked -> loadNasaImages(event.startDate, event.endDate)
            is SearchEvent.OnStartDateClicked -> showDatePicker(DateType.START)
            is SearchEvent.OnEndDateClicked -> showDatePicker(DateType.END)
            is SearchEvent.OnDateSelected -> onDateSelected(event.type, event.calendar)
            is SearchEvent.OnNasaImageClicked -> navigateToDetails(event.date)
            SearchEvent.OnErrorDialogDismissed -> dismissErrorDialog()
        }
    }

    private fun dismissErrorDialog() {
        _state.update { it.copy(globalError = "") }
    }

    private fun loadNasaImages(startDate: String, endDate: String) {
        validateInputs(startDate, endDate)?.let { errorMessage ->
            _state.update { it.copy(fieldError = errorMessage) }
            return
        }

        _state.update { it.copy(isLoading = true, fieldError = "") }

        val cachedData = cacheManager.get(startDate, endDate)
        if (cachedData != null) {
            _state.update {
                it.copy(
                    isLoading = false,
                    nasaImages = cachedData
                )
            }
            viewModelScope.launch {
                _effect.emit(SearchEffect.ShowToast(resourceProvider.getString(R.string.from_cache)))
            }
            return
        }

        viewModelScope.launch {
            _effect.emit(SearchEffect.ShowToast(resourceProvider.getString(R.string.from_api)))
            runCatching {
                getNasaImagesUseCase(startDate, endDate)
            }.fold(
                onSuccess = {
                    nasaImages ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            nasaImages = nasaImages,
                        )
                    }
                    cacheManager.put(startDate, endDate, nasaImages)
                },
                onFailure = { e -> handleError(e) }
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

    private suspend fun handleError(throwable: Throwable) {
        val errorMessage = when (throwable) {
            is ForbiddenAccessException ->
                throwable.message ?: resourceProvider
                    .getString(ru.fav.starlight.presentation.R.string.error_forbidden_access)
            is NetworkException -> resourceProvider
                .getString(ru.fav.starlight.presentation.R.string.error_network)
            is ServerException -> throwable.message ?: resourceProvider
                .getString(ru.fav.starlight.presentation.R.string.error_server)
            else -> resourceProvider
                .getString(ru.fav.starlight.presentation.R.string.error_unknown)
        }
        _state.update {
            it.copy(
                isLoading = false,
                globalError = errorMessage
            )
        }
    }

    private fun loadInitialDates() {
        val currentDate = dateProvider.getCurrentDate()
        val formattedCurrentDate = dateProvider.formatDate(currentDate)
        _state.update {
            it.copy(
                startDate = formattedCurrentDate,
                endDate = formattedCurrentDate
            )
        }
    }

    private fun onDateSelected(dateType: DateType, calendar: Calendar) {
        val date = dateProvider.formatDate(calendar)
        _state.update { state ->
            when(dateType) {
                DateType.START ->
                    state.copy(
                    isLoading = false,
                    startDate = date)
                DateType.END -> state.copy(
                    isLoading = false,
                    endDate = date)
            }
        }
    }

    private fun showDatePicker(type: DateType) {
        viewModelScope.launch {
            val currentDateString = when (type) {
                DateType.START -> _state.value.startDate
                DateType.END -> _state.value.endDate
            }
            val initialCalendar = dateProvider.parseDate(currentDateString)

            val minDateCalendar = Calendar.getInstance().apply {
                set(1995, Calendar.JUNE, 16)
            }

            _effect.emit(SearchEffect.ShowDatePicker(
                type = type,
                maxDateMillis = dateProvider.getCurrentDate().timeInMillis,
                minDateMillis = minDateCalendar.timeInMillis,
                initialDate = initialCalendar
            ))
        }
    }

    private fun navigateToDetails(date: String) {
        navMain.goToDetailsPage(date)
    }

    override fun onCleared() {
        super.onCleared()
    }
}

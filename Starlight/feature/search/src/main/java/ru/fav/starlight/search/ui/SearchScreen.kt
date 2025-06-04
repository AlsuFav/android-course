package ru.fav.starlight.search.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.fav.starlight.search.ui.state.SearchEvent
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import ru.fav.starlight.search.R
import ru.fav.starlight.search.ui.state.SearchEffect
import java.util.Calendar
import androidx.compose.runtime.remember
import ru.fav.starlight.search.ui.component.DateInputField
import ru.fav.starlight.search.ui.component.NasaImagesGrid
import ru.fav.starlight.search.ui.component.ShimmerGrid

@Composable
fun SearchScreen(
    viewModel: SearchViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val onImageClick = remember(viewModel) {
        { date: String -> viewModel.reduce(SearchEvent.OnNasaImageClicked(date)) }
    }

    val context = LocalContext.current
    LaunchedEffect(key1 = viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SearchEffect.ShowDatePicker -> {
                    val calendar = effect.initialDate
                    DatePickerDialog(
                        context,
                        { _, year, month, day ->
                            val selectedDate = Calendar.getInstance().apply { set(year, month, day) }
                            viewModel.reduce(SearchEvent.OnDateSelected(effect.type, selectedDate))
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).apply {
                        datePicker.minDate = effect.minDateMillis
                        datePicker.maxDate = effect.maxDateMillis
                        show()
                    }
                }
                is SearchEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    if (!state.globalError.isEmpty()) {
        AlertDialog(
            onDismissRequest = {
                viewModel.reduce(SearchEvent.OnErrorDialogDismissed)
            },
            title = { Text(text = stringResource(ru.fav.starlight.presentation.R.string.error_title)) },
            text = { Text(text = state.globalError) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.reduce(SearchEvent.OnErrorDialogDismissed)
                    }
                ) {
                    Text(text = stringResource(ru.fav.starlight.presentation.R.string.ok))
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DateInputField(
                label = stringResource(R.string.start_date),
                value = state.startDate,
                onClick = remember { { viewModel.reduce(SearchEvent.OnStartDateClicked) } },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            DateInputField(
                label = stringResource(R.string.end_date),
                value = state.endDate,
                onClick = remember { { viewModel.reduce(SearchEvent.OnEndDateClicked) } },
                modifier = Modifier.weight(1f)
            )
        }

        if (! state.fieldError.isEmpty()) {
            Text(
                text = state.fieldError,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.reduce(SearchEvent.OnFetchImagesClicked(state.startDate, state.endDate))
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading,
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(stringResource(R.string.fetch_nasa_images))
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (state.isLoading) {
            ShimmerGrid()
        } else if (state.nasaImages.isNotEmpty()) {
            NasaImagesGrid(
                images = state.nasaImages,
                onImageClick = onImageClick
            )
        }
    }
}
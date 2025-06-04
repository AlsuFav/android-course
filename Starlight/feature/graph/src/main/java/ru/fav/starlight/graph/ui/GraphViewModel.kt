package ru.fav.starlight.graph.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ru.fav.starlight.domain.provider.ResourceProvider
import ru.fav.starlight.graph.ui.state.GraphEvent
import ru.fav.starlight.graph.ui.state.GraphState
import javax.inject.Inject
import ru.fav.starlight.graph.R

@HiltViewModel
class GraphViewModel @Inject constructor(
    private val resourceProvider: ResourceProvider,
) : ViewModel() {

    private val _state = MutableStateFlow(GraphState())
    val state: StateFlow<GraphState> = _state.asStateFlow()

    fun onEvent(event: GraphEvent) {
        when (event) {
            is GraphEvent.PointCountChanged -> {
                _state.update {
                    it.copy(
                        pointCount = sanitizePointsInput(event.count),
                        fieldError = ""
                    )
                }
            }
            is GraphEvent.ValuesChanged -> {
                _state.update {
                    it.copy(
                        values = sanitizeValuesInput(event.values),
                        fieldError = ""
                    )
                }
            }
            is GraphEvent.DrawGraph -> {
                drawGraph()
            }
        }
    }

    private fun sanitizePointsInput(input: String): String {
        return input
            .filter { it.isDigit() }
    }

    private fun sanitizeValuesInput(input: String): String {
        return input
            .filter { it.isDigit() || it == ',' || it == '.' }
            .replace(",,", ",")
            .replace("..", ".")
            .replace(".,", ",")
            .replace(",.", ",")
            .removePrefix(",")
            .removePrefix(".")
    }

    private fun drawGraph() {
        val count = _state.value.pointCount.toIntOrNull()
        val values = parseValues(_state.value.values)

        validateInputs(count, values)?.let { errorMessage ->
            _state.update {
                it.copy(
                    fieldError = errorMessage,
                    showGraph = false
                )
            }
            return
        }

        _state.update {
            it.copy(
                valuesList = values!!,
                fieldError = "",
                showGraph = true
            )
        }
    }

    private fun parseValues(input: String): List<Float>? {
        return if (input.isBlank()) {
            null
        } else {
            input.split(",")
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .mapNotNull { it.toFloatOrNull() }
        }
    }

    private fun validateInputs(count: Int?, values: List<Float>?): String? {
        return when {
            count == null || values == null ->
                resourceProvider.getString(R.string.error_fill_all_fields)
            count <= 1 ->
                resourceProvider.getString(R.string.error_points_count)
            values.size != count ->
                resourceProvider.getString(R.string.error_values_count_not_the_same)
            else -> null
        }
    }
}

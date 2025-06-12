package ru.fav.starlight.diagram.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.core.graphics.toColorInt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.fav.starlight.diagram.ui.state.DiagramEvent
import ru.fav.starlight.diagram.ui.state.DiagramState
import kotlin.random.Random

@HiltViewModel
class DiagramViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow<DiagramState>(DiagramState.Initial)
    val state: StateFlow<DiagramState> = _state.asStateFlow()

    fun reduce(event: DiagramEvent) {
        when (event) {
            is DiagramEvent.OnButtonClicked -> generateDiagramData()
        }
    }

    private val DIAGRAM_COLORS = listOf(
        "#FFC107".toColorInt(),
        "#4CAF50".toColorInt(),
        "#2196F3".toColorInt(),
        "#E91E63".toColorInt(),
        "#9C27B0".toColorInt(),
        "#FF5722".toColorInt(),
        "#00BCD4".toColorInt(),
        "#795548".toColorInt(),
        "#607D8B".toColorInt(),
        "#F44336".toColorInt(),
        "#009688".toColorInt(),
        "#3F51B5".toColorInt()
    )

    private val MIN_SECTORS = 2
    private val MAX_SECTORS = 7

    private fun generateDiagramData() {
        val numberOfSectors = Random.nextInt(MIN_SECTORS, MAX_SECTORS + 1)

        val values = mutableListOf<Int>()
        repeat(numberOfSectors) {
            values.add(Random.nextInt(1, 101))
        }

        val colors = DIAGRAM_COLORS.shuffled().take(numberOfSectors)

        _state.value = DiagramState.Success(values, colors)
    }
}

package ru.fav.starlight.diagram.ui.state

sealed class DiagramState {
    object Initial : DiagramState()
    data class Success(val values: List<Int>, val colors: List<Int>) : DiagramState()
}

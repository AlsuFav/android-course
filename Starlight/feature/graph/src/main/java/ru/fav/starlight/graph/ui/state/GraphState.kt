package ru.fav.starlight.graph.ui.state

import androidx.compose.runtime.Immutable

@Immutable
data class GraphState(
    val pointCount: String = "",
    val values: String = "",
    val valuesList: List<Float> = emptyList(),
    val fieldError: String = "",
    val showGraph: Boolean = false
)

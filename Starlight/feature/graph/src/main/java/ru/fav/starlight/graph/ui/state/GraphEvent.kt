package ru.fav.starlight.graph.ui.state

sealed class GraphEvent {
    data class PointCountChanged(val count: String) : GraphEvent()
    data class ValuesChanged(val values: String) : GraphEvent()
    object DrawGraph : GraphEvent()
}

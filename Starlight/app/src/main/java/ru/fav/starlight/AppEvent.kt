package ru.fav.starlight

import android.os.Bundle

sealed class AppEvent {
    data class ConditionalFeatureRequest(
        val featureActionId: Int,
        val args: Bundle? = null,
        val authorized: Boolean = false,
    ) : AppEvent()
}
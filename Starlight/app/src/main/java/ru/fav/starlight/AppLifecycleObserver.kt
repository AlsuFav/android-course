package ru.fav.starlight

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLifecycleObserver @Inject constructor() : DefaultLifecycleObserver {

    private var _isAppInForeground: Boolean = false
    val isAppInForeground: Boolean
        get() = _isAppInForeground

    fun register() {
        ProcessLifecycleOwner.get().lifecycle.removeObserver(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        _isAppInForeground = true
    }

    override fun onStop(owner: LifecycleOwner) {
        _isAppInForeground = false
    }
}
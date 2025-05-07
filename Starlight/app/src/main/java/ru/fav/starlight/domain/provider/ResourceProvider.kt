package ru.fav.starlight.domain.provider

interface ResourceProvider {
    fun getString(stringResId: Int): String
    fun getString(stringResId: Int, vararg args: Any): String
}

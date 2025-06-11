package ru.fav.starlight.data.util

data class HttpErrorModel(
    val error: HttpError = HttpError()
)

data class HttpError(
    val code: String = "",
    val message: String = ""
)

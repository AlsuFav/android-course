package ru.fav.starlight.domain.model

data class HttpErrorModel(
    val error: HttpError = HttpError()
)

data class HttpError(
    val code: String = "",
    val message: String = ""
)

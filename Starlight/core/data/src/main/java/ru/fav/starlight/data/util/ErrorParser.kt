package ru.fav.starlight.data.util

import com.google.gson.Gson

object ErrorParser {

    fun parseHttpError(errorBody: String?): HttpErrorModel? {
        return try {
            errorBody?.let {
                Gson().fromJson(it, HttpErrorModel::class.java)
            }
        } catch (_: Exception) {
            null
        }
    }
}

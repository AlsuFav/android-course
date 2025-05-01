package ru.fav.starlight.data.remote.pojo

import com.google.gson.annotations.SerializedName

data class NasaImageResponse(
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("date")
    val date: String? = null,
    @SerializedName("explanation")
    val explanation: String? = null,
    @SerializedName("url")
    val imageUrl: String? = null,
    @SerializedName("hdurl")
    val hdImageUrl: String? = null
)

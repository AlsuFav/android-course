package ru.fav.starlight.network

import retrofit2.http.GET
import retrofit2.http.Query
import ru.fav.starlight.network.pojo.NasaImageResponse

interface NasaApi {

    @GET("planetary/apod")
    suspend fun getNasaImages(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): List<NasaImageResponse>?

    @GET("planetary/apod")
    suspend fun getNasaImageDetails(
        @Query("date") date: String
    ): NasaImageResponse?
}

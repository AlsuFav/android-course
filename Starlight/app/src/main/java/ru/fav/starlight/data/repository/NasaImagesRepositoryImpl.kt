package ru.fav.starlight.data.repository

import retrofit2.HttpException
import ru.fav.starlight.data.mapper.NasaImageResponseMapper
import ru.fav.starlight.data.remote.NasaApi
import ru.fav.starlight.data.util.ErrorParser.parseHttpError
import ru.fav.starlight.data.util.HttpStatusCodes
import ru.fav.starlight.domain.exception.ForbiddenAccessException
import ru.fav.starlight.domain.exception.NetworkException
import ru.fav.starlight.domain.exception.ServerException
import ru.fav.starlight.domain.model.NasaImageDetailsModel
import ru.fav.starlight.domain.model.NasaImageModel
import ru.fav.starlight.domain.repository.NasaImagesRepository
import java.io.IOException
import javax.inject.Inject

class NasaImagesRepositoryImpl @Inject constructor(
    private val nasaApi: NasaApi,
    private val mapper: NasaImageResponseMapper,
): NasaImagesRepository {
    override suspend fun getNasaImages(
        startDate: String,
        endDate: String
    ): List<NasaImageModel> {
        return try {
            val response = nasaApi.getNasaImages(
                startDate = startDate,
                endDate = endDate
            )
            mapper.mapNasaImageList(response)
        } catch (_: IOException) {
            throw NetworkException(null)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val httpError = parseHttpError(errorBody)

            when (e.code()) {
                HttpStatusCodes.FORBIDDEN -> throw ForbiddenAccessException(httpError?.error?.message)
                else -> throw ServerException(httpError?.error?.message)
            }
        }
    }

    override suspend fun getNasaImageDetails(date: String): NasaImageDetailsModel {
        return try {
            val response = nasaApi.getNasaImageDetails(date)
            mapper.mapNasaImageDetails(response)
        } catch (_: IOException) {
            throw NetworkException(null)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val httpError = parseHttpError(errorBody)

            when (e.code()) {
                HttpStatusCodes.FORBIDDEN -> throw ForbiddenAccessException(httpError?.error?.message)
                else -> throw ServerException(httpError?.error?.message)
            }
        }
    }
}

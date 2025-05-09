package ru.fav.starlight.domain.exception

class ForbiddenAccessException(message: String?) : Exception(message)
class NoApiKeyException(message: String?) : Exception(message)

class NetworkException(message: String?) : Exception(message)
class ServerException(message: String?) : Exception(message)

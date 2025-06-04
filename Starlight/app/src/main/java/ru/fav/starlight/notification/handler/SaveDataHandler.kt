package ru.fav.starlight.notification.handler

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.fav.starlight.domain.di.qualifier.IoDispatchers
import ru.fav.starlight.domain.usecase.SaveApiKeyUseCase
import ru.fav.starlight.notification.FcmMessageHandler
import ru.fav.starlight.notification.util.FcmCategories
import ru.fav.starlight.notification.util.FcmDataKeys
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveDataHandler @Inject constructor(
    private val saveApiKeyUseCase: SaveApiKeyUseCase,
    @IoDispatchers private val dispatcher: CoroutineDispatcher
) : FcmMessageHandler {

    override fun canHandle(category: String): Boolean = 
        category == FcmCategories.SAVE_DATA

    override fun handle(data: Map<String, String>) {
        data[FcmDataKeys.API_KEY]?.let { apiKey ->
            CoroutineScope(dispatcher).launch {
                saveApiKeyUseCase(apiKey)
            }
        }
    }
}
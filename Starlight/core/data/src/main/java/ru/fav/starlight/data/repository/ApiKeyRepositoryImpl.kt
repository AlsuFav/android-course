package ru.fav.starlight.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import ru.fav.starlight.domain.repository.ApiKeyRepository
import javax.inject.Inject

class ApiKeyRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ApiKeyRepository {
    
    companion object {
        val API_KEY_KEY = stringPreferencesKey("api_key")
    }
    
    override suspend fun saveApiKey(apiKey: String) {
        dataStore.edit { preferences ->
            preferences[API_KEY_KEY] = apiKey
        }
    }
    
    override suspend fun getApiKey(): String? {
        return dataStore.data
            .map { preferences -> preferences[API_KEY_KEY] }
            .firstOrNull()
    }
    
    override suspend fun clearApiKey() {
        dataStore.edit { preferences ->
            preferences.remove(API_KEY_KEY)
        }
    }
}

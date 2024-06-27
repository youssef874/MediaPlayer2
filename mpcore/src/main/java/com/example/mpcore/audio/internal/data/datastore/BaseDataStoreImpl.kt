package com.example.mpcore.audio.internal.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.example.mpcore.logger.api.data.MPLoggerLevel
import com.example.mpcore.logger.internal.MPLoggerConfiguration
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal abstract class BaseDataStoreImpl(
    protected var dataStore: DataStore<Preferences>,
    protected val dispatcher: CoroutineDispatcher = Dispatchers.IO
): IBaseDataStore {

    companion object{
        private const val CLASS_NAME = "BaseDataStoreImpl"
        private const val TAG = "DATA_STORE"
    }

    override fun getAll(): Flow<Map<String, Any>> {
        return dataStore.data.map {
            it.asMap().entries.associate { entry ->
                entry.key.toString() to entry.value
            }
        }
    }

    override suspend fun clear() {
        withContext(dispatcher){
            dataStore.edit {
                MPLoggerConfiguration.DefaultBuilder().log(
                    className = CLASS_NAME,
                    tag = TAG,
                    methodName = "clear",
                    logLevel = MPLoggerLevel.DEBUG,
                    msg = "remove all data from data store"
                )
                it.clear()
            }
        }
    }
}
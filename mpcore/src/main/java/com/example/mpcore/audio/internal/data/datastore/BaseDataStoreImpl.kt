package com.example.mpcore.audio.internal.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.example.mpcore.logger.api.data.MPLoggerLevel
import com.example.mpcore.logger.internal.MPLoggerConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal abstract class BaseDataStoreImpl(
    protected var dataStore: DataStore<Preferences>
): IBaseDataStore {

    companion object{
        const val FILE_NAME = "mp_datastore"
        private const val CLASS_NAME = "BaseDataStoreImpl"
        private const val TAG = "DATA_STORE"
    }

    override fun getAll(): Flow<Map<String, Any>> {
        return dataStore.data.map {
            it.asMap().entries.map {entry ->
                entry.key.toString() to entry.value
            }.toMap()
        }
    }

    override suspend fun clear() {
        withContext(Dispatchers.IO){
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
package com.example.mpcore.audio.internal.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import com.example.mpcore.logger.api.data.MPLoggerLevel
import com.example.mpcore.logger.internal.MPLoggerConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * An [Long] implementation for [IDataStoreModifier]
 */
internal class LongDataStoreModifierImpl(
    dataStore: DataStore<Preferences>
): BaseDataStoreImpl(dataStore), IDataStoreModifier<Long> {

    companion object{
        private const val CLASS_NAME = "LongDataStoreModifierImpl"
        private const val TAG = "DATA_STORE"
    }

    override suspend fun put(key: String, data: Long) {
        withContext(Dispatchers.IO){
            MPLoggerConfiguration.DefaultBuilder().log(
                className = CLASS_NAME,
                tag = TAG,
                methodName = "put",
                logLevel = MPLoggerLevel.DEBUG,
                msg = "key: $key, value: $data"
            )
            val longPreferenceKey = longPreferencesKey(key)
            dataStore.edit {
                it[longPreferenceKey] = data
            }
        }
    }

    override fun observe(key: String, defaultValue: Long): Flow<Long> {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME,
            tag = TAG,
            methodName = "observe",
            logLevel = MPLoggerLevel.DEBUG,
            msg = "key: $key, defaultValue: $defaultValue"
        )
        return dataStore.data.map {
            try {
                val longPreferenceKey = longPreferencesKey(key)
                it[longPreferenceKey]?:defaultValue
            }catch (e: ClassCastException){
                defaultValue
            }
        }
    }

    override suspend fun remove(key: String) {
        withContext(Dispatchers.IO){
            dataStore.edit {pref->
                MPLoggerConfiguration.DefaultBuilder().log(
                    className = CLASS_NAME,
                    tag = TAG,
                    methodName = "remove",
                    logLevel = MPLoggerLevel.DEBUG,
                    msg = "key: $key"
                )
                val longPreferenceKey = floatPreferencesKey(key)
                pref.remove(longPreferenceKey)
            }
        }
    }

    override suspend fun contains(key: String): Boolean {
        return withContext(Dispatchers.IO){
            val longPreferenceKey = longPreferencesKey(key)
            dataStore.data.map {
                if (it.asMap().isEmpty()){
                    false
                }else{
                    it.contains(longPreferenceKey)
                }
            }.first()
        }
    }
}
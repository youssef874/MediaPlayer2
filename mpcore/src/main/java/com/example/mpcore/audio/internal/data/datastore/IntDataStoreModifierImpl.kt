package com.example.mpcore.audio.internal.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.example.mpcore.logger.api.data.MPLoggerLevel
import com.example.mpcore.logger.internal.MPLoggerConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * An [Int] implementation for [IDataStoreModifier]
 */
internal class IntDataStoreModifierImpl(
    dataStore: DataStore<Preferences>
): BaseDataStoreImpl(dataStore), IDataStoreModifier<Int> {

    companion object{
        private const val CLASS_NAME = "IntDataStoreModifierImpl"
        private const val TAG = "DATA_STORE"
    }

    override suspend fun put(key: String, data: Int) {
        withContext(Dispatchers.IO){
            MPLoggerConfiguration.DefaultBuilder().log(
                className = CLASS_NAME,
                tag = TAG,
                methodName = "put",
                logLevel = MPLoggerLevel.DEBUG,
                msg = "key: $key, value: $data"
            )
            val intPreferenceKey = intPreferencesKey(key)
            dataStore.edit {
                it[intPreferenceKey] = data
            }
        }
    }

    override fun observe(key: String, defaultValue: Int): Flow<Int> {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME,
            tag = TAG,
            methodName = "observe",
            logLevel = MPLoggerLevel.DEBUG,
            msg = "key: $key, defaultValue: $defaultValue"
        )
        return dataStore.data.map {
            try {
                val intPreferenceKey = intPreferencesKey(key)
                it[intPreferenceKey]?:defaultValue
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
                val intPreferenceKey = intPreferencesKey(key)
                pref.remove(intPreferenceKey)
            }
        }
    }

    override suspend fun contains(key: String): Boolean {
        return withContext(Dispatchers.IO){
            val intPreferenceKey = intPreferencesKey(key)
            dataStore.data.map {
                if (it.asMap().isEmpty()){
                    false
                }else{
                    it.contains(intPreferenceKey)
                }
            }.first()
        }
    }
}
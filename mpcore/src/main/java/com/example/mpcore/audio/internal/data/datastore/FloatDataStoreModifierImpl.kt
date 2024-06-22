package com.example.mpcore.audio.internal.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import com.example.mpcore.logger.api.data.MPLoggerLevel
import com.example.mpcore.logger.internal.MPLoggerConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * An [Float] implementation for [IDataStoreModifier]
 */
internal class FloatDataStoreModifierImpl(
    dataStore: DataStore<Preferences>
): BaseDataStoreImpl(dataStore), IDataStoreModifier<Float> {

    companion object{
        private const val CLASS_NAME = "FloatDataStoreModifierImpl"
        private const val TAG = "DATA_STORE"
    }

    override suspend fun put(key: String, data: Float) {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME,
            tag = TAG,
            methodName = "put",
            logLevel = MPLoggerLevel.DEBUG,
            msg = "key: $key, value: $data"
        )
        withContext(Dispatchers.IO){
            val floatPreferenceKey = floatPreferencesKey(key)
            dataStore.edit {
                it[floatPreferenceKey] = data
            }
        }
    }

    override fun observe(key: String, defaultValue: Float): Flow<Float> {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME,
            tag = TAG,
            methodName = "observe",
            logLevel = MPLoggerLevel.DEBUG,
            msg = "key: $key, defaultValue: $defaultValue"
        )
        return dataStore.data.map {
            try {
                val floatPreferenceKey = floatPreferencesKey(key)
                it[floatPreferenceKey]?:defaultValue
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
                val floatPreferenceKey = floatPreferencesKey(key)
                pref.remove(floatPreferenceKey)
            }
        }
    }

    override suspend fun contains(key: String): Boolean {
       return withContext(Dispatchers.IO){
           val floatPreferenceKey = floatPreferencesKey(key)
           dataStore.data.map {
               if (it.asMap().isEmpty()){
                   false
               }else{
                   it.contains(floatPreferenceKey)
               }
           }.first()
       }
    }
}
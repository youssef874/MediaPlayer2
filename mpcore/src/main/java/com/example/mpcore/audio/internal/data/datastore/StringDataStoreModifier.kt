package com.example.mpcore.audio.internal.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.mpcore.logger.api.data.MPLoggerLevel
import com.example.mpcore.logger.internal.MPLoggerConfiguration
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * An [String] implementation for [IDataStoreModifier]
 */
internal class StringDataStoreModifier(
    dataStore: DataStore<Preferences>,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
): BaseDataStoreImpl(dataStore,dispatcher), IDataStoreModifier<String> {

    companion object{
        private const val CLASS_NAME = "StringDataStoreModifier"
        private const val TAG = "DATA_STORE"
    }

    override suspend fun put(key: String, data: String) {
        withContext(dispatcher){
            MPLoggerConfiguration.DefaultBuilder().log(
                className = CLASS_NAME,
                tag = TAG,
                methodName = "put",
                logLevel = MPLoggerLevel.DEBUG,
                msg = "key: $key, value: $data"
            )
            dataStore.edit {
                it[stringPreferencesKey(key)] = data
            }
        }
    }

    override fun observe(key: String, defaultValue: String): Flow<String> {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME,
            tag = TAG,
            methodName = "observe",
            logLevel = MPLoggerLevel.DEBUG,
            msg = "key: $key, defaultValue: $defaultValue"
        )
        return dataStore.data.map {
            try {
                val stringPreferenceKey = stringPreferencesKey(key)
                it[stringPreferenceKey]?:defaultValue
            }catch (e: ClassCastException){
                defaultValue
            }
        }.flowOn(dispatcher)
    }

    override suspend fun remove(key: String) {
        withContext(dispatcher){
            dataStore.edit {pref->
                MPLoggerConfiguration.DefaultBuilder().log(
                    className = CLASS_NAME,
                    tag = TAG,
                    methodName = "remove",
                    logLevel = MPLoggerLevel.DEBUG,
                    msg = "key: $key"
                )
                val stringPreferenceKey = stringPreferencesKey(key)
                pref.remove(stringPreferenceKey)
            }
        }
    }

    override suspend fun contains(key: String): Boolean {
        return withContext(dispatcher){
            val stringPreferenceKey = stringPreferencesKey(key)
            dataStore.data.map {
                if (it.asMap().isEmpty()){
                    false
                }else{
                    it.contains(stringPreferenceKey)
                }
            }.first()
        }
    }
}
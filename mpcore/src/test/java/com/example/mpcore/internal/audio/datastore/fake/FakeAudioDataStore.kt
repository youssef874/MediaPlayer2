package com.example.mpcore.internal.audio.datastore.fake

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.mpcore.audio.internal.data.datastore.IAudioDataStore
import com.example.mpcore.audio.internal.data.datastore.IBaseDataStore
import com.example.mpcore.audio.internal.data.datastore.IDataStoreModifier
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import java.util.concurrent.ConcurrentHashMap

internal object FakeAudioDataStore: IAudioDataStore {

    private val dataStoreCache = ConcurrentHashMap<String,Any>()
    private val consumer = ConcurrentHashMap<String,suspend (Any)->Unit>()
    private var onDataChanged: (suspend ()->Unit)? = null

    private suspend fun putInt(key: String, value: Int) {
        dataStoreCache[key] = value
        consumer[key]?.invoke(value)
        onDataChanged?.invoke()
    }

    private suspend fun putLong(key: String, value: Long) {
        dataStoreCache[key] = value
        consumer[key]?.invoke(value)
        onDataChanged?.invoke()
    }

    private suspend fun putFloat(key: String, value: Float) {
        dataStoreCache[key] = value
        consumer[key]?.invoke(value)
        onDataChanged?.invoke()
    }

    private suspend fun putString(key: String, value: String) {
        dataStoreCache[key] = value
        consumer[key]?.invoke(value)
        onDataChanged?.invoke()
    }

    private suspend fun putBoolean(key: String, value: Boolean) {
        dataStoreCache[key] = value
        consumer[key]?.invoke(value)
        onDataChanged?.invoke()
    }

    private fun getInt(key: String, defaultValue: Int): Flow<Int> {
        return callbackFlow {
            send(dataStoreCache[key] as Int? ?:defaultValue)
            consumer[key] = {
                send(it as Int)
            }
            awaitClose { consumer[key] }
        }
    }

    private fun getLong(key: String, defaultValue: Long): Flow<Long> {
        return callbackFlow {
            send(dataStoreCache[key] as Long? ?:defaultValue)
            consumer[key] = {
                send(it as Long)
            }
            awaitClose { consumer[key] }
        }
    }

    private fun getFloat(key: String, defaultValue: Float): Flow<Float> {
        return callbackFlow {
            send(dataStoreCache[key] as Float? ?:defaultValue)
            consumer[key] = {
                send(it as Float)
            }
            awaitClose { consumer[key] }
        }
    }

    private fun getString(key: String, defaultValue: String): Flow<String> {
        return callbackFlow {
            send(dataStoreCache[key] as String? ?:defaultValue)
            consumer[key] = {
                send(it as String)
            }
            awaitClose { consumer[key] }
        }
    }

    private fun getBoolean(key: String, defaultValue: Boolean): Flow<Boolean> {
        return callbackFlow {
            send(dataStoreCache[key] as Boolean? ?:defaultValue)
            consumer[key] = {
                send(it as Boolean)
            }
            awaitClose{consumer.remove(key)}
        }
    }

    private fun getAll(): Flow<Map<String, Any>> {
        return callbackFlow {
            send(dataStoreCache)
            onDataChanged = {
                send(dataStoreCache)
            }
            awaitClose { onDataChanged = null }
        }
    }

    private suspend fun removeValue(key: String) {
        dataStoreCache.remove(key)
        onDataChanged?.invoke()
    }

    private suspend fun <T> isContain(key: Preferences.Key<T>): Boolean {
        return dataStoreCache.contains(key.name)
    }

    private suspend fun clearDS() {
        dataStoreCache.clear()
        onDataChanged?.invoke()
    }

    override fun getIntDataStoreModifier(): IDataStoreModifier<Int> = object : BaseDataStore(),IDataStoreModifier<Int>{
        override suspend fun put(key: String, data: Int) {
            putInt(key,data)
        }

        override fun observe(key: String, defaultValue: Int): Flow<Int> {
            return getInt(key, defaultValue)
        }

        override suspend fun remove(key: String) {
            removeValue(key)
        }

        override suspend fun contains(key: String): Boolean {
            return isContain(intPreferencesKey(key))
        }

    }

    override fun getStringDataStoreModifier(): IDataStoreModifier<String> = object : BaseDataStore(),IDataStoreModifier<String>{
        override suspend fun put(key: String, data: String) {
            putString(key,data)
        }

        override fun observe(key: String, defaultValue: String): Flow<String> {
            return getString(key, defaultValue)
        }

        override suspend fun remove(key: String) {
            removeValue(key)
        }

        override suspend fun contains(key: String): Boolean {
            return isContain(stringPreferencesKey(key))
        }

    }

    override fun getBooleanDataStoreModifier(): IDataStoreModifier<Boolean> = object : BaseDataStore(),IDataStoreModifier<Boolean>{
        override suspend fun put(key: String, data: Boolean) {
            putBoolean(key,data)
        }

        override fun observe(key: String, defaultValue: Boolean): Flow<Boolean> {
            return getBoolean(key, defaultValue)
        }

        override suspend fun remove(key: String) {
            removeValue(key)
        }

        override suspend fun contains(key: String): Boolean {
           return isContain(booleanPreferencesKey(key))
        }

    }

    override fun getFloatDataStoreModifier(): IDataStoreModifier<Float> = object : BaseDataStore(),IDataStoreModifier<Float>{
        override suspend fun put(key: String, data: Float) {
            putFloat(key,data)
        }

        override fun observe(key: String, defaultValue: Float): Flow<Float> {
            return getFloat(key, defaultValue)
        }

        override suspend fun remove(key: String) {
            removeValue(key)
        }

        override suspend fun contains(key: String): Boolean {
            return isContain(floatPreferencesKey(key))
        }

    }

    override fun getLongDataStoreModifier(): IDataStoreModifier<Long> = object : BaseDataStore(), IDataStoreModifier<Long>{
        override suspend fun put(key: String, data: Long) {
            putLong(key,data)
        }

        override fun observe(key: String, defaultValue: Long): Flow<Long> {
            return getLong(key, defaultValue)
        }

        override suspend fun remove(key: String) {
            removeValue(key)
        }

        override suspend fun contains(key: String): Boolean {
            return isContain(longPreferencesKey(key))
        }

    }

    private abstract class BaseDataStore: IBaseDataStore{
        override fun getAll(): Flow<Map<String, Any>> {
            return getAll()
        }

        override suspend fun clear() {
            clearDS()
        }

    }
}
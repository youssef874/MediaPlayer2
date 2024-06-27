package com.example.mpcore.audio.internal.data.datastore

import com.example.mpcore.audio.api.data.model.RepeatMode
import com.example.mpcore.audio.internal.data.datastore.data.AudioDataStoreModel
import com.example.mpcore.logger.api.data.MPLoggerLevel
import com.example.mpcore.logger.internal.MPLoggerConfiguration
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

internal class AudioDatastoreDataProviderImpl(
    audioDataStore: IAudioDataStore
): IAudioDatastoreDataProvider {

    companion object{
        private const val CLASS_NAME = "AudioDatastoreDataProviderImpl"
        private const val TAG = "DATASTORE"
    }

    private val booleanDataStoreModifier = audioDataStore.getBooleanDataStoreModifier()

    private val longDataStoreModifier = audioDataStore.getLongDataStoreModifier()

    private val intDataStoreModifier = audioDataStore.getIntDataStoreModifier()

    override fun getIsDatabaseSynchronizedWithContentProvider(model: AudioDataStoreModel<Boolean>): IAudioDataStoreController<Boolean> {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME,
            tag = TAG,
            methodName = "getIsDatabaseSynchronizedWithContentProvider",
            msg = "key: ${model.key}, defaultValue: ${model.defaultValue}",
            logLevel = MPLoggerLevel.INFO
        )
        return object : IAudioDataStoreController<Boolean>{
            override suspend fun updateValue(data: Boolean) {
                booleanDataStoreModifier.put(key = model.key, data = data)
            }

            override fun observeValue(): Flow<Boolean> {
                return booleanDataStoreModifier.observe(key = model.key, defaultValue = model.defaultValue)
            }

            override suspend fun getValue(): Boolean {
                return observeValue().firstOrNull()?:model.defaultValue
            }

        }
    }

    override fun getIsSDKSynchronizedProvider(model: AudioDataStoreModel<Boolean>): IAudioDataStoreController<Boolean> {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME,
            tag = TAG,
            methodName = "getIsSDKSynchronizedProvider",
            msg = "key: ${model.key}, defaultValue: ${model.defaultValue}",
            logLevel = MPLoggerLevel.INFO
        )
        return object : IAudioDataStoreController<Boolean>{
            override suspend fun updateValue(data: Boolean) {
                booleanDataStoreModifier.put(key = model.key, data = data)
            }

            override fun observeValue(): Flow<Boolean> {
                return booleanDataStoreModifier.observe(key = model.key, defaultValue = model.defaultValue)
            }

            override suspend fun getValue(): Boolean {
                return observeValue().firstOrNull()?:model.defaultValue
            }

        }
    }

    override fun getLastPlayingSongId(model: AudioDataStoreModel<Long>): IAudioDataStoreController<Long> {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME,
            tag = TAG,
            methodName = "getLastPlayingSongId",
            msg = "key: ${model.key}, defaultValue: ${model.defaultValue}",
            logLevel = MPLoggerLevel.INFO
        )
        return object : IAudioDataStoreController<Long>{
            override suspend fun updateValue(data: Long) {
                longDataStoreModifier.put(model.key,data)
            }

            override fun observeValue(): Flow<Long> {
                return longDataStoreModifier.observe(model.key,model.defaultValue)
            }

            override suspend fun getValue(): Long {
                return observeValue().first()
            }

        }
    }

    override fun getAudioProgression(model: AudioDataStoreModel<Int>): IAudioDataStoreController<Int> {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME,
            tag = TAG,
            methodName = "getAudioProgression",
            msg = "key: ${model.key}, defaultValue: ${model.defaultValue}",
            logLevel = MPLoggerLevel.INFO
        )
        return object : IAudioDataStoreController<Int>{
            override suspend fun updateValue(data: Int) {
                intDataStoreModifier.put(model.key,data)
            }

            override fun observeValue(): Flow<Int> {
                return intDataStoreModifier.observe(model.key,model.defaultValue)
            }

            override suspend fun getValue(): Int {
                return observeValue().first()
            }

        }
    }

    override fun getIsInRandomMode(model: AudioDataStoreModel<Boolean>): IAudioDataStoreController<Boolean> {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME,
            tag = TAG,
            methodName = "getInInRandomMode",
            msg = "key: ${model.key}, defaultValue: ${model.defaultValue}",
            logLevel = MPLoggerLevel.INFO
        )
        return object : IAudioDataStoreController<Boolean>{
            override suspend fun updateValue(data: Boolean) {
                booleanDataStoreModifier.put(model.key,data)
            }

            override fun observeValue(): Flow<Boolean> {
                return booleanDataStoreModifier.observe(model.key,model.defaultValue)
            }

            override suspend fun getValue(): Boolean {
                return observeValue().first()
            }

        }
    }

    override fun getRepeatMode(model: AudioDataStoreModel<Int>): IAudioDataStoreController<Int> {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME,
            tag = TAG,
            methodName = "getRepeatMode",
            msg = "key: ${model.key}, defaultValue: ${model.defaultValue}",
            logLevel = MPLoggerLevel.INFO
        )
        return object : IAudioDataStoreController<@RepeatMode Int>{
            override suspend fun updateValue(data: Int) {
                intDataStoreModifier.put(model.key,data)
            }

            override fun observeValue(): Flow<Int> {
                return intDataStoreModifier.observe(model.key,model.defaultValue)
            }

            override suspend fun getValue(): Int {
                return observeValue().first()
            }

        }
    }

    override fun getDatabaseVersionController(model: AudioDataStoreModel<Int>): IAudioDataStoreController<Int> {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME,
            tag = TAG,
            methodName = "getDatabaseVersionController",
            msg = "key: ${model.key}, defaultValue: ${model.defaultValue}",
            logLevel = MPLoggerLevel.INFO
        )
        return object : IAudioDataStoreController<Int>{
            override suspend fun updateValue(data: Int) {
                intDataStoreModifier.put(model.key,data)
            }

            override fun observeValue(): Flow<Int> {
                return intDataStoreModifier.observe(model.key,model.defaultValue)
            }

            override suspend fun getValue(): Int {
                return observeValue().first()
            }
        }
    }
}
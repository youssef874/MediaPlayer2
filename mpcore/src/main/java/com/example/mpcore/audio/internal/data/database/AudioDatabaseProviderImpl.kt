package com.example.mpcore.audio.internal.data.database

import com.example.mpcore.audio.internal.data.database.model.AudioEntity
import com.example.mpcore.logger.api.data.MPLoggerLevel
import com.example.mpcore.logger.internal.MPLoggerConfiguration
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

internal class AudioDatabaseProviderImpl(
    private val audioDao: AudioDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
): IAudioDatabaseDataProvider {

    companion object{
        private const val CLASS_NAME = "AudioDatabaseProviderImpl"
        private const val TAG = "DATA_BASE"
    }

    override suspend fun changeAudioFavoriteStatus(isFavorite: Boolean, audioId: Long): Boolean {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME,
            tag = TAG,
            methodName = "changeAudioFavoriteStatus",
            msg = "isFavorite: $isFavorite, audioId: $audioId",
            logLevel = MPLoggerLevel.DEBUG
        )
        return withContext(dispatcher){
            val result = audioDao.changeAudioFavoriteStatus(isFavorite, audioId)
            MPLoggerConfiguration.DefaultBuilder().log(
                className = CLASS_NAME,
                tag = TAG,
                methodName = "changeAudioFavoriteStatus",
                msg = "update result: $result",
                logLevel = MPLoggerLevel.DEBUG
            )
            result == 1
        }
    }

    override suspend fun add(data: AudioEntity): Long {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME,
            tag = TAG,
            methodName = "add",
            msg = "try to add $data",
            logLevel = MPLoggerLevel.DEBUG
        )
        return withContext(dispatcher){
            audioDao.insert(data)
        }
    }

    override suspend fun update(data: AudioEntity): Boolean {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME,
            tag = TAG,
            methodName = "update",
            msg = "try to update with $data",
            logLevel = MPLoggerLevel.DEBUG
        )
        return withContext(dispatcher){
             audioDao.update(data) > 0
        }
    }

    override suspend fun delete(data: AudioEntity) {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME,
            tag = TAG,
            methodName = "delete",
            msg = "try to delete $data",
            logLevel = MPLoggerLevel.DEBUG
        )
        withContext(dispatcher){
            audioDao.delete(data)
        }
    }

    override suspend fun getById(id: Long): AudioEntity? {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME,
            tag = TAG,
            methodName = "getById",
            msg = "id: $id",
            logLevel = MPLoggerLevel.DEBUG
        )
        return withContext(dispatcher){
            audioDao.getById(id)
        }
    }

    override fun observeById(id: Long): Flow<AudioEntity?> {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME,
            tag = TAG,
            methodName = "observeById",
            msg = "id: $id",
            logLevel = MPLoggerLevel.DEBUG
        )
        return audioDao.observeById(id)
    }

    override suspend fun getAll(): List<AudioEntity> {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME,
            tag = TAG,
            methodName = "getAll",
            msg = "try to get all audio",
            logLevel = MPLoggerLevel.DEBUG
        )
        return withContext(dispatcher){
            audioDao.getAll()
        }
    }

    override fun observeAll(): Flow<List<AudioEntity>> {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME,
            tag = TAG,
            methodName = "observeAll",
            msg = "try to get all audio",
            logLevel = MPLoggerLevel.DEBUG
        )
        return audioDao.observeAll()
    }

    override suspend fun deleteAll() {
        withContext(dispatcher){
            audioDao.deleteAll()
        }
    }
}
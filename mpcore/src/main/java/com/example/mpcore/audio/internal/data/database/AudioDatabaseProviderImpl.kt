package com.example.mpcore.audio.internal.data.database

import com.example.mpcore.audio.internal.data.database.model.AudioEntity
import com.example.mpcore.logger.api.data.MPLoggerLevel
import com.example.mpcore.logger.internal.MPLoggerConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

internal class AudioDatabaseProviderImpl(
    private val audioDao: AudioDao
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
        val result = audioDao.changeAudioFavoriteStatus(isFavorite, audioId)
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME,
            tag = TAG,
            methodName = "changeAudioFavoriteStatus",
            msg = "update result: $result",
            logLevel = MPLoggerLevel.DEBUG
        )
        return if (result == 1) true else false
    }

    override suspend fun add(data: AudioEntity): Long {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME,
            tag = TAG,
            methodName = "add",
            msg = "try to add $data",
            logLevel = MPLoggerLevel.DEBUG
        )
        return withContext(Dispatchers.IO){
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
        return withContext(Dispatchers.IO){
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
        withContext(Dispatchers.IO){
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
        return withContext(Dispatchers.IO){
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
        return withContext(Dispatchers.IO){
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
        withContext(Dispatchers.IO){
            audioDao.deleteAll()
        }
    }
}
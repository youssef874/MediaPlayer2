package com.example.mpcore.internal.audio.synchronize.database

import com.example.mpcore.audio.internal.data.database.AudioDao
import com.example.mpcore.audio.internal.data.database.IAudioDatabaseDataProvider
import com.example.mpcore.audio.internal.data.database.model.AudioEntity
import kotlinx.coroutines.flow.Flow

internal class FakeAudioDatabaseDataProvider(
    private val audioDao: AudioDao,
): IAudioDatabaseDataProvider {
    override suspend fun changeAudioFavoriteStatus(isFavorite: Boolean, audioId: Long): Boolean {
        return audioDao.changeAudioFavoriteStatus(isFavorite, audioId) == 1
    }

    override suspend fun add(data: AudioEntity): Long {
        return audioDao.insert(data)
    }

    override suspend fun update(data: AudioEntity): Boolean {
        return audioDao.update(data) > 0
    }

    override suspend fun delete(data: AudioEntity) {
        audioDao.delete(data)
    }

    override suspend fun getById(id: Long): AudioEntity? {
        return audioDao.getById(id)
    }

    override fun observeById(id: Long): Flow<AudioEntity?> {
        return audioDao.observeById(id)
    }

    override suspend fun getAll(): List<AudioEntity> {
        return audioDao.getAll()
    }

    override fun observeAll(): Flow<List<AudioEntity>> {
        return audioDao.observeAll()
    }

    override suspend fun deleteAll() {
        audioDao.deleteAll()
    }
}
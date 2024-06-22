package com.example.mpcore.audio.internal.data.database

import com.example.mpcore.audio.api.exception.AddAudioToPlayListThatAlreadyExist
import com.example.mpcore.audio.internal.data.database.model.AudioEntity
import com.example.mpcore.audio.internal.data.database.model.PlayListEntity
import com.example.mpcore.logger.api.data.MPLoggerLevel
import com.example.mpcore.logger.internal.MPLoggerConfiguration
import com.example.mpstorage.database.internal.entity.PlaylistSongCrossRef
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

internal class PlayListDatabaseDataProviderImpl(
    private val playListDao: IPlayListDao,
    private val audioDao: AudioDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : IPlayListDatabaseDataProvider {

    companion object {
        private const val CLASS_NAME = "PlayListDatabaseDataProviderImpl"
        private const val TAG = "DATA_BASE"
    }

    override suspend fun attachPlayListToAudio(audioId: Long, playListEntity: PlayListEntity) {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME,
            tag = TAG,
            methodName = "attachPlayListToAudio",
            msg = "attach audioId: $audioId to playListEntity: $playListEntity",
            logLevel = MPLoggerLevel.INFO
        )
        withContext(dispatcher) {
            val playlistWithSongs = audioDao.getListOfAudioForPlayList(playListEntity.id)
            playlistWithSongs?.songs?.firstOrNull { it.id == audioId }?.let {
                throw AddAudioToPlayListThatAlreadyExist(audioId, playListEntity.name)
            }
            val playList = playListDao.getPlayListById(playListEntity.id)
            var id: Long? = null
            if (playList == null) {
                id = playListDao.insert(playListEntity)
            }
            playListDao.addPlaylistSongCrossRef(
                PlaylistSongCrossRef(playListId = id ?: playListEntity.id, songId = audioId)
            )
        }
    }

    override suspend fun getFirstAudioOfPlayList(playListId: Long): AudioEntity? {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME,
            tag = TAG,
            methodName = "getFirstAudioOfPlayList",
            msg = "audioId: $playListId",
            logLevel = MPLoggerLevel.INFO
        )
        return withContext(dispatcher) {
            audioDao.getListOfAudioForPlayList(playListId = playListId)?.songs?.firstOrNull()
        }
    }

    override suspend fun getAudioListOfPlaylist(playListId: Long): List<AudioEntity> {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME,
            tag = TAG,
            methodName = "getAudioListOfPlaylist",
            msg = "audioId: $playListId",
            logLevel = MPLoggerLevel.INFO
        )
        return withContext(dispatcher) {
            audioDao.getListOfAudioForPlayList(playListId)?.songs ?: emptyList()
        }
    }

    override suspend fun add(data: PlayListEntity): Long {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME,
            tag = TAG,
            methodName = "add",
            msg = "add data: $data",
            logLevel = MPLoggerLevel.INFO
        )
        return withContext(dispatcher) {
            playListDao.insert(data)
        }
    }

    override suspend fun update(data: PlayListEntity): Boolean {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME,
            tag = TAG,
            methodName = "update",
            msg = "update data: $data",
            logLevel = MPLoggerLevel.INFO
        )
        return withContext(dispatcher) {
            playListDao.update(data) > 0
        }
    }

    override suspend fun delete(data: PlayListEntity) {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME,
            tag = TAG,
            methodName = "delete",
            msg = "add data: $data",
            logLevel = MPLoggerLevel.INFO
        )
        withContext(dispatcher) {
            playListDao.delete(data)
        }
    }

    override suspend fun getById(id: Long): PlayListEntity? {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME,
            tag = TAG,
            methodName = "getById",
            msg = "id: $id",
            logLevel = MPLoggerLevel.INFO
        )
        return withContext(dispatcher) {
            playListDao.getPlayListById(id)
        }
    }

    override fun observeById(id: Long): Flow<PlayListEntity?> {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME,
            tag = TAG,
            methodName = "observeById",
            msg = "id: $id",
            logLevel = MPLoggerLevel.INFO
        )
        return playListDao.observeById(id)
    }

    override suspend fun getAll(): List<PlayListEntity> {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME,
            tag = TAG,
            methodName = "getAll",
            msg = "return all playlist",
            logLevel = MPLoggerLevel.INFO
        )
        return withContext(dispatcher) {
            playListDao.getAll()
        }
    }

    override fun observeAll(): Flow<List<PlayListEntity>> {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME,
            tag = TAG,
            methodName = "observeAll",
            msg = "all playlist",
            logLevel = MPLoggerLevel.INFO
        )
        return playListDao.observeAll()
    }

    override suspend fun deleteAll() {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME,
            tag = TAG,
            methodName = "deleteAll",
            msg = "all playlist",
            logLevel = MPLoggerLevel.INFO
        )
        withContext(dispatcher) {
            playListDao.deleteAll()
        }
    }


}
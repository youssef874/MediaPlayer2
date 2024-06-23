package com.example.mpcore.internal.audio

import com.example.mpcore.audio.api.exception.AddAudioToPlayListThatAlreadyExist
import com.example.mpcore.audio.internal.data.database.AudioDao
import com.example.mpcore.audio.internal.data.database.IPlayListDao
import com.example.mpcore.audio.internal.data.database.IPlayListDatabaseDataProvider
import com.example.mpcore.audio.internal.data.database.model.AudioEntity
import com.example.mpcore.audio.internal.data.database.model.PlayListEntity
import com.example.mpstorage.database.internal.entity.PlaylistSongCrossRef
import kotlinx.coroutines.flow.Flow

internal class FakePlayListDatabaseDataProvider(
    private val playListDao: IPlayListDao,
    private val audioDao: AudioDao
): IPlayListDatabaseDataProvider {
    override suspend fun attachPlayListToAudio(audioId: Long, playListEntity: PlayListEntity) {
        if (audioDao.getById(audioId) == null){
            throw NoSuchElementException("no audio by id: $audioId found please add one before attache it to some playlist")
        }
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

    suspend fun addAudio(audioEntity: AudioEntity): Long{
        return audioDao.insert(audioEntity)
    }

    suspend fun deleteAllAudio(){
        audioDao.deleteAll()
    }

    override suspend fun getFirstAudioOfPlayList(playListId: Long): AudioEntity? {
        return audioDao.getListOfAudioForPlayList(playListId = playListId)?.songs?.firstOrNull()
    }

    override suspend fun getAudioListOfPlaylist(playListId: Long): List<AudioEntity> {
        return audioDao.getListOfAudioForPlayList(playListId)?.songs ?: emptyList()
    }

    override suspend fun add(data: PlayListEntity): Long {
        return playListDao.insert(data)
    }

    override suspend fun update(data: PlayListEntity): Boolean {
        return playListDao.update(data) > 0
    }

    override suspend fun delete(data: PlayListEntity) {
        playListDao.delete(data)
    }

    override suspend fun getById(id: Long): PlayListEntity? {
        return playListDao.getPlayListById(id)
    }

    override fun observeById(id: Long): Flow<PlayListEntity?> {
        return playListDao.observeById(id)
    }

    override suspend fun getAll(): List<PlayListEntity> {
        return playListDao.getAll()
    }

    override fun observeAll(): Flow<List<PlayListEntity>> {
        return playListDao.observeAll()
    }

    override suspend fun deleteAll() {
        playListDao.deleteAll()
    }
}
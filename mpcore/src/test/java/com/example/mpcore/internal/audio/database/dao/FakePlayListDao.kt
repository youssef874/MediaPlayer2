package com.example.mpcore.internal.audio.database.dao

import com.example.mpcore.audio.internal.data.database.IPlayListDao
import com.example.mpcore.audio.internal.data.database.model.PlayListEntity
import com.example.mpcore.audio.internal.data.database.model.SongWithPlaylists
import com.example.mpstorage.database.internal.entity.PlaylistSongCrossRef
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class FakePlayListDao private constructor(
    private val fakeAudioDao: FakeAudioDao
): IPlayListDao {
    
    private val dataList = mutableListOf<PlayListEntity>()
    var notify: (suspend ()->Unit)? = null

    var notifyCrossRefChanges: (suspend ()->Unit)? = null
    private val songWithPlaylistsData= mutableListOf<SongWithPlaylists>()
    
    override suspend fun addPlaylistSongCrossRef(playlistSongCrossRef: PlaylistSongCrossRef) {
        songWithPlaylistsData.firstOrNull { it.audioEntity.id == playlistSongCrossRef.songId }?.let { songWithPlaylists ->
            val list = songWithPlaylists.playLists.toMutableList()
            dataList.firstOrNull { it.id == playlistSongCrossRef.playListId }?.let { playListEntity ->
                list.add(playListEntity)
            }
            songWithPlaylistsData[songWithPlaylistsData.indexOf(songWithPlaylists)] = SongWithPlaylists(playLists = list, audioEntity = songWithPlaylists.audioEntity)
        }?: kotlin.run {
            val audio = fakeAudioDao.getById(playlistSongCrossRef.songId)
            val list = mutableListOf<PlayListEntity>()
            dataList.firstOrNull { it.id == playlistSongCrossRef.playListId }?.let {
                list.add(it)
            }
            audio?.let {audioEntity ->
                songWithPlaylistsData.add(SongWithPlaylists(audioEntity = audioEntity, playLists = list))
            }
        }
        notifyCrossRefChanges?.invoke()
        dataList.firstOrNull { it.id == playlistSongCrossRef.playListId }?.let {
            fakeAudioDao.audioAttachedToPlaylist(playlistSongCrossRef.songId,it)
        }
    }

    override suspend fun getPlayListById(id: Long): PlayListEntity? {
        return dataList.firstOrNull { it.id == id }
    }

    override fun observeById(id: Long): Flow<PlayListEntity> {
        return flow {
            dataList.firstOrNull { it.id == id }?.let { emit(it) }
            notify={
                dataList.firstOrNull { it.id == id }?.let { emit(it) }
            }
        }
    }

    override fun getAll(): List<PlayListEntity> {
        return dataList
    }

    override fun observeAll(): Flow<List<PlayListEntity>> {
        return flow {
            emit(dataList)
            notify={
                emit(dataList)
            }
        }
    }

    override suspend fun deleteAll() {
        songWithPlaylistsData.clear()
        dataList.clear()
    }

    override suspend fun getListOfPlayListForAudio(audioId: Long): SongWithPlaylists? {
        return songWithPlaylistsData.firstOrNull { it.audioEntity.id == audioId }
    }

    override fun observeListOfPlayListForAudio(audioId: Long): Flow<SongWithPlaylists?> {
        return flow {
            emit(songWithPlaylistsData.firstOrNull { it.audioEntity.id == audioId })
            notifyCrossRefChanges = {
                emit(songWithPlaylistsData.firstOrNull { it.audioEntity.id == audioId })
            }
        }
    }

    override suspend fun getListOfPlayListOfSongs(): List<SongWithPlaylists> {
        return songWithPlaylistsData
    }

    override fun observeListOfPlayListOfSongs(): Flow<List<SongWithPlaylists>> {
        return flow {
            emit(songWithPlaylistsData)
            notifyCrossRefChanges={
                emit(songWithPlaylistsData)
            }
        }
    }

    override suspend fun insert(data: PlayListEntity): Long {
        val id = generateId()
        val playListEntity = data.copy(id = id)
        dataList.add(playListEntity)
        notify?.invoke()
        return id
    }

    override suspend fun update(data: PlayListEntity): Int {
        dataList.firstOrNull { it.id == data.id }?.let {
            dataList[dataList.indexOf(it)] = data
            notify?.invoke()
            return 1
        }
        return -1
    }

    override suspend fun delete(data: PlayListEntity) {
        dataList.firstOrNull { it.id == data.id }?.let {
            dataList.remove(it)
            notify?.invoke()
        }
    }

    private fun generateId(): Long{
        return if (dataList.isEmpty()){
            0L
        }else{
            dataList.sortBy { it.id }
            dataList.last().id+1
        }
    }

    companion object{

        private var INSTANCE: FakePlayListDao? = null

        fun getInstance(audioDao: FakeAudioDao): FakePlayListDao = INSTANCE?: synchronized(this){
            val instance = FakePlayListDao(audioDao)
            INSTANCE = instance
            instance
        }
    }
}
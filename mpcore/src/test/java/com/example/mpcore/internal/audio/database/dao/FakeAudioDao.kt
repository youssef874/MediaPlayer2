package com.example.mpcore.internal.audio.database.dao

import com.example.mpcore.audio.internal.data.database.AudioDao
import com.example.mpcore.audio.internal.data.database.model.AudioEntity
import com.example.mpstorage.database.internal.entity.PlayListEntity
import com.example.mpstorage.database.internal.entity.PlaylistWithSongs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow

internal object FakeAudioDao: AudioDao {

    private val dataList = mutableListOf<AudioEntity>()
    var notify: (suspend ()->Unit)? = null
    private val playlistWithSongsList = mutableListOf<PlaylistWithSongs>()
    var noTifyPlaylistWithSongsList: (suspend (PlaylistWithSongs)->Unit)? = null

    override suspend fun getById(id: Long): AudioEntity? {
        return dataList.firstOrNull { it.id == id }
    }

    override fun observeById(id: Long): Flow<AudioEntity?> {
        return channelFlow {
            send(dataList.firstOrNull { it.id == id })
            notify = {
                send(dataList.firstOrNull { it.id == id })
            }
        }
    }

    override suspend fun getAll(): List<AudioEntity> {
        return dataList
    }

    override fun observeAll(): Flow<List<AudioEntity>> {
        return channelFlow {
            send(dataList)
            notify = {
                send(dataList)
            }
        }
    }

    override suspend fun deleteAll() {
        dataList.clear()
    }

    override suspend fun changeAudioFavoriteStatus(isFavorite: Boolean, audioId: Long): Int {
        val audioEntity = dataList.firstOrNull { it.id == audioId }
        val newAudioEntity = audioEntity?.copy(isFavorite = isFavorite)
        audioEntity?.let {
            if (newAudioEntity != null) {
                dataList[dataList.indexOf(it)] = newAudioEntity
            }
            return 1
        }
        return 0
    }

    override suspend fun getListOfAudioListOfPlayList(): List<PlaylistWithSongs> {
        return playlistWithSongsList
    }

    override suspend fun getListOfAudioForPlayList(playListId: Long): PlaylistWithSongs? {
        return playlistWithSongsList.firstOrNull { it.playListEntity.id == playListId }
    }

    override fun observeListOfAudioForPlayList(playListId: Long): Flow<PlaylistWithSongs?> {
        return flow {
            emit(playlistWithSongsList.firstOrNull { it.playListEntity.id == playListId })
        }
    }

    override fun observeListOfAudioListOfPlayList(): Flow<List<PlaylistWithSongs>> {
        return flow {
            emit(playlistWithSongsList)
        }
    }

    suspend fun audioAttachedToPlaylist(audioId: Long, playListEntity: PlayListEntity){
        dataList.firstOrNull { it.id == audioId }?.let {audioEntity ->
            playlistWithSongsList.firstOrNull { it.playListEntity.id == playListEntity.id }?.let { playlistWithSongs ->
                val audioList = playlistWithSongs.songs.toMutableList()
                if (audioList.none { it.id == audioId }){
                    audioList.add(audioEntity)
                }
                val newData = PlaylistWithSongs(playListEntity = playlistWithSongs.playListEntity, songs = audioList)
                playlistWithSongsList[playlistWithSongsList.indexOf(playlistWithSongs)] = newData
                noTifyPlaylistWithSongsList?.invoke(newData)
            }?: kotlin.run {
                playlistWithSongsList.add(
                    PlaylistWithSongs(
                        playListEntity = playListEntity, songs = listOf(audioEntity)
                    )
                )
            }
        }
    }

    override suspend fun insert(data: AudioEntity): Long {

         val id = generateId()
        val audioEntity = data.copy(id = id)
        dataList.add(audioEntity)
        notify?.invoke()
        return id
    }

    override suspend fun update(data: AudioEntity): Int {
        dataList.firstOrNull { it.id == data.id }?.let {
            dataList[dataList.indexOf(it)] = data
            notify?.invoke()
            return 1
        }
        return -1
    }

    override suspend fun delete(data: AudioEntity) {
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
}
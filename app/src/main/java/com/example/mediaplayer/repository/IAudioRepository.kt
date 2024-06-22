package com.example.mediaplayer.repository

import android.net.Uri
import com.example.mpcore.audio.api.data.MPApiResult
import com.example.mpcore.audio.api.data.model.MPAudio
import com.example.mpcore.audio.api.data.model.MPPlayList
import com.example.mpcore.audio.internal.data.datastore.IAudioDataStoreController
import kotlinx.coroutines.flow.Flow

interface IAudioRepository {

    suspend fun getAllSong(): MPApiResult<List<MPAudio>>

    fun observeAllAudio(): Flow<MPApiResult<List<MPAudio>>>

    fun observeAllPlayList(): Flow<MPApiResult<List<MPPlayList>>>

    suspend fun attachPlayListToAudio(audioId: Long, mpPlayList: MPPlayList): MPApiResult<Unit>

    suspend fun getFirstAudioOfPlayList(playListId: Long): MPApiResult<MPAudio?>

    fun observeSongById(songId: Long): Flow<MPApiResult<MPAudio?>>

    suspend fun changeAudioFavoriteStatus(isFavorite: Boolean, audioId: Long): Boolean

    fun getLastPlayingSongId(): IAudioDataStoreController<Long>

    fun getIsPlaying(): Boolean

    fun playSong(uri: Uri,playAt: Int = -1): Boolean

    fun pauseSong(): Boolean

    fun resumeSong(): Boolean

    fun stopCurrentPlayingSong(): Boolean

    fun setOnSongCompletionListener(onSongCompletion: ()->Unit)

    suspend fun getAudioProgress(): Int

    fun observeAudioProgress(): Flow<Int>

    fun updateCurrentPlayerPosition(position: Int): Boolean
}
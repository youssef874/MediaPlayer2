package com.example.mediaplayer.domain

import com.example.mediaplayer.domain.model.UiAudio
import com.example.mediaplayer.domain.model.UiPlayList
import com.example.mpcore.audio.api.data.MPApiResult
import kotlinx.coroutines.flow.Flow

interface IFetchDataUseCase {

    suspend fun getAllSong(): MPApiResult<List<UiAudio>>

    fun observeAllAudio(): Flow<MPApiResult<List<UiAudio>>>

    fun observeAllPlayList(): Flow<MPApiResult<List<UiPlayList>>>

    suspend fun attachPlayListToAudio(audioId: Long, uiPlayList: UiPlayList): MPApiResult<Unit>

    fun observeSongById(songId: Long): Flow<MPApiResult<UiAudio?>>

    val cachedAudioList: List<UiAudio>

    fun updateCachedAudioList(list: List<UiAudio>)

    suspend fun getLastPlayingSongId(): Long

    suspend fun getLastPlayingSong(): UiAudio?
}
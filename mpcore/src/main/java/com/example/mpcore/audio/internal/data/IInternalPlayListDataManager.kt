package com.example.mpcore.audio.internal.data

import com.example.mpcore.audio.api.data.MPApiResult
import com.example.mpcore.audio.api.data.model.MPAudio
import com.example.mpcore.audio.api.data.model.MPPlayList
import kotlinx.coroutines.flow.Flow

internal interface IInternalPlayListDataManager {

    suspend fun getAllPlayList(): MPApiResult<List<MPPlayList>>

    fun observeAllPlayList(): Flow<MPApiResult<List<MPPlayList>>>

    suspend fun attachPlayListToAudio(audioId: Long, mpPlayList: MPPlayList): MPApiResult<Unit>

    suspend fun getFirstAudioOfPlayList(playListId: Long): MPApiResult<MPAudio?>

}
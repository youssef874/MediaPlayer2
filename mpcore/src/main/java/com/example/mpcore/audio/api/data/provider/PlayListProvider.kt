package com.example.mpcore.audio.api.data.provider

import com.example.mpcore.audio.api.data.MPApiResult
import com.example.mpcore.audio.api.data.model.MPAudio
import com.example.mpcore.audio.api.data.model.MPPlayList
import com.example.mpcore.audio.internal.data.IInternalPlayListDataManager
import kotlinx.coroutines.flow.Flow

internal class PlayListProvider(
    private val internalPlayListDataManager: IInternalPlayListDataManager
): IPlayListProvider {

    override suspend fun getAllPlayList(): MPApiResult<List<MPPlayList>> {
        return internalPlayListDataManager.getAllPlayList()
    }

    override fun observeAllPlayList(): Flow<MPApiResult<List<MPPlayList>>> {
        return internalPlayListDataManager.observeAllPlayList()
    }

    override suspend fun attachPlayListToAudio(audioId: Long, mpPlayList: MPPlayList): MPApiResult<Unit> {
        return internalPlayListDataManager.attachPlayListToAudio(audioId, mpPlayList)
    }

    override suspend fun getFirstAudioOfPlayList(playListId: Long): MPApiResult<MPAudio?> {
        return internalPlayListDataManager.getFirstAudioOfPlayList(playListId)
    }
}
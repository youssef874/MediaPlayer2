package com.example.mpcore.audio.internal.data

import com.example.mpcore.audio.api.data.MPApiResult
import com.example.mpcore.audio.api.data.model.MPAudio
import com.example.mpcore.audio.api.data.model.RepeatMode
import com.example.mpcore.audio.internal.data.datastore.IAudioDataStoreController
import kotlinx.coroutines.flow.Flow

internal interface IInternalDataManager {

    suspend fun deleteAllAudio()

    suspend fun getAllAudio(): MPApiResult<List<MPAudio>>

    fun observeAllAudio(): Flow<MPApiResult<List<MPAudio>>>

    fun observeAudioById(audioId: Long): Flow<MPApiResult<MPAudio?>>

    fun getLastPlayingSongId(): IAudioDataStoreController<Long>

    suspend fun changeAudioFavoriteStatus(isFavorite: Boolean, audioId: Long): Boolean

    fun isInRandomMode(): IAudioDataStoreController<Boolean>

    fun repeatMode(): IAudioDataStoreController<@RepeatMode Int>
}
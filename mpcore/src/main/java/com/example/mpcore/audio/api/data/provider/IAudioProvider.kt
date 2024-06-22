package com.example.mpcore.audio.api.data.provider

import com.example.mpcore.audio.api.data.MPApiResult
import com.example.mpcore.audio.api.data.model.MPAudio
import com.example.mpcore.audio.api.data.model.RepeatMode
import com.example.mpcore.audio.internal.data.datastore.IAudioDataStoreController
import kotlinx.coroutines.flow.Flow

/**
 * This abstract represent the link between internal and api
 */
internal interface IAudioProvider {

    suspend fun deleteAllAudio()

    suspend fun getAllAudio(): MPApiResult<List<MPAudio>>

    fun observeAll(): Flow<MPApiResult<List<MPAudio>>>

    fun observeById(audioId: Long): Flow<MPApiResult<MPAudio?>>

    fun getLastPlayingSongId(): IAudioDataStoreController<Long>

    suspend fun changeAudioFavoriteStatus(isFavorite: Boolean, audioId: Long): Boolean

    fun isInRandomMode(): IAudioDataStoreController<Boolean>

    fun repeatMode(): IAudioDataStoreController<@RepeatMode Int>
}
package com.example.mpcore.audio.api.data.provider

import com.example.mpcore.audio.api.data.MPApiResult
import com.example.mpcore.audio.api.data.model.MPAudio
import com.example.mpcore.audio.internal.data.IInternalDataManager
import com.example.mpcore.audio.internal.data.datastore.IAudioDataStoreController
import kotlinx.coroutines.flow.Flow

/**
 * This class represent as intermediate between data api and internal data module
 */
internal class AudioProviderImpl(private val internalDataManager: IInternalDataManager) :
    IAudioProvider {


    override suspend fun deleteAllAudio() {
        internalDataManager.deleteAllAudio()
    }

    override suspend fun getAllAudio(): MPApiResult<List<MPAudio>> {
        return internalDataManager.getAllAudio()
    }

    override fun observeAll(): Flow<MPApiResult<List<MPAudio>>> {

        return internalDataManager.observeAllAudio()
    }

    override fun observeById(audioId: Long): Flow<MPApiResult<MPAudio?>> {
        return internalDataManager.observeAudioById(audioId)
    }

    override fun getLastPlayingSongId(): IAudioDataStoreController<Long> {
        return internalDataManager.getLastPlayingSongId()
    }

    override suspend fun changeAudioFavoriteStatus(isFavorite: Boolean, audioId: Long): Boolean {
        return internalDataManager.changeAudioFavoriteStatus(isFavorite, audioId)
    }

    override fun isInRandomMode(): IAudioDataStoreController<Boolean> {
        return internalDataManager.isInRandomMode()
    }

    override fun repeatMode(): IAudioDataStoreController<Int> {
        return internalDataManager.repeatMode()
    }
}
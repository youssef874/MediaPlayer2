package com.example.mpcore.audio.api.data

import com.example.mpcore.audio.api.data.model.MPAudio
import com.example.mpcore.audio.api.data.model.MPPlayList
import com.example.mpcore.audio.api.data.model.RepeatMode
import com.example.mpcore.audio.api.data.provider.AudioProviderImpl
import com.example.mpcore.audio.api.data.provider.PlayListProvider
import com.example.mpcore.audio.internal.data.datastore.IAudioDataStoreController
import com.example.mpcore.audio.internal.data.factory.InternalDataManagerFactory
import com.example.mpcore.audio.internal.data.factory.InternalPlayListDataManagerFactory
import com.example.mpcore.common.internal.SDKComponent
import kotlinx.coroutines.flow.Flow

object MPAudioDataApi {

    private val audioProvider = AudioProviderImpl(InternalDataManagerFactory.create(SDKComponent.SDKContext.getContext()))

    private val playListProvider = PlayListProvider(InternalPlayListDataManagerFactory.create(SDKComponent.SDKContext.getContext()))

    /**
     * Get all available songs
     * @return [MPApiResult] of list of [MPAudio]
     */
    suspend fun getAllAudio(): MPApiResult<List<MPAudio>> {
        return audioProvider.getAllAudio()
    }

    /**
     * Get all available playlist
     * @return [MPApiResult] of list of [MPPlayList]
     */
    suspend fun getAllPlayList(): MPApiResult<List<MPPlayList>>{
        return playListProvider.getAllPlayList()
    }

    /**
     * Observe all available playlist in real time
     * @return flow of [MPApiResult] of list of [MPPlayList]
     */
    fun observeAllPlayList(): Flow<MPApiResult<List<MPPlayList>>>{
        return playListProvider.observeAllPlayList()
    }

    /**
     * Observe all available songs in real time
     * @return flow of [MPApiResult] of list of [MPAudio]
     */
    fun observeAllAudio(): Flow<MPApiResult<List<MPAudio>>>{
        return audioProvider.observeAll()
    }

    /**
     * Observe thz requested song in real time
     * @param audioId: the requested song id
     * @return flow of [MPApiResult]  of [MPAudio]
     */
    fun observeAudioById(audioId: Long): Flow<MPApiResult<MPAudio?>>{
        return audioProvider.observeById(audioId)
    }

    /**
     * add song to playlist
     * @param audioId: the song to be added
     * @param mpPlayList: the playlist to be added to
     * @return [MPApiResult] success if the operation we success otherwise will return error
     * [DataCode.AUDIO_ALREADY_ATTACHED_TO_PLAYLIST] or [DataCode.SQL_FAILED] or [DataCode.ELEMENT_NOT_FOUND]
     */
    suspend fun attachPlayListToAudio(audioId: Long, mpPlayList: MPPlayList): MPApiResult<Unit>{
        return playListProvider.attachPlayListToAudio(audioId, mpPlayList)
    }

    /**
     * Get the first audio in requested playlist if there any
     * @param playListId: the id of the requested playlist
     * @return the first [MPAudio] of playlist or null otherwise
     */
    suspend fun getFirstAudioOfPlayList(playListId: Long): MPApiResult<MPAudio?> = playListProvider.getFirstAudioOfPlayList(playListId)

    /**
     * get last playing song id
     * @return [IAudioDataStoreController] if getValue or observeValue return -1 mean no value assigned to last
     * playing song id field
     */
    fun getLastPlayingSongId(): IAudioDataStoreController<Long>{
        return audioProvider.getLastPlayingSongId()
    }

    /**
     * random mode controller
     * @return [IAudioDataStoreController]
     */
    fun getIsInRandomMode(): IAudioDataStoreController<Boolean>{
        return audioProvider.isInRandomMode()
    }

    /**
     * repeat mode controller
     * @return [IAudioDataStoreController] getValue or observeValue will return [RepeatMode]
     */
    fun repeatMode(): IAudioDataStoreController<@RepeatMode Int>{
        return audioProvider.repeatMode()
    }

    /**
     * Change the song to as favorite or to remove it from the favorite
     * @param isFavorite: if to be favorite or not
     * @param audioId: the song id to be changed
     * @return true if the update was success otherwise will return false
     */
    suspend fun changeAudioFavoriteStatus(isFavorite: Boolean, audioId: Long): Boolean{
        return audioProvider.changeAudioFavoriteStatus(isFavorite,audioId)
    }

}
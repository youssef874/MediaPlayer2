package com.example.mpcore.audio.internal.data.datastore

import com.example.mpcore.audio.api.data.model.RepeatMode
import com.example.mpcore.audio.internal.data.datastore.data.AudioDataStoreModel

/**
 * This represent as intermediate between datastore and datastore manager
 */
internal interface IAudioDatastoreDataProvider {

    /**
     * Call this to get action for isDatabaseSynchronizedWithContentProvider
     * @param model: a [Boolean] instance of [AudioDataStoreModel]
     * @return a [Boolean] implementation of [IAudioDataStoreController]
     */
    fun getIsDatabaseSynchronizedWithContentProvider(model: AudioDataStoreModel<Boolean>): IAudioDataStoreController<Boolean>

    /**
     * Call this to get action for isSDKSynchronized
     * @param model: a [Boolean] instance of [AudioDataStoreModel]
     * @return a [Boolean] implementation of [IAudioDataStoreController]
     */
    fun getIsSDKSynchronizedProvider(model: AudioDataStoreModel<Boolean>): IAudioDataStoreController<Boolean>

    /**
     * Call this to get action for lastPlayingSongId
     * @param model: a [Long] instance of [AudioDataStoreModel]
     * @return a [Long] implementation of [IAudioDataStoreController]
     */
    fun getLastPlayingSongId(model: AudioDataStoreModel<Long>): IAudioDataStoreController<Long>

    /**
     * Call this to get action for audioProgression
     * @param model: a [Int] instance of [AudioDataStoreModel]
     * @return a [Int] implementation of [IAudioDataStoreController]
     */
    fun getAudioProgression(model: AudioDataStoreModel<Int>): IAudioDataStoreController<Int>

    /**
     * Call this to get action for isInRandomMode
     * @param model: a [Boolean] instance of [AudioDataStoreModel]
     * @return a [Boolean] implementation of [IAudioDataStoreController]
     */
    fun getIsInRandomMode(model: AudioDataStoreModel<Boolean>): IAudioDataStoreController<Boolean>

    /**
     * Call this to get action for repeatMode
     * @param model: a [Int] instance of [AudioDataStoreModel]
     * @return a [Int] implementation of [IAudioDataStoreController]
     */
    fun getRepeatMode(model: AudioDataStoreModel<@RepeatMode Int>): IAudioDataStoreController<@RepeatMode Int>

    /**
     * Call this to get action for databaseVersionController
     * @param model: a [Int] instance of [AudioDataStoreModel]
     * @return a [Int] implementation of [IAudioDataStoreController]
     */
    fun getDatabaseVersionController(model: AudioDataStoreModel<Int>): IAudioDataStoreController<Int>
}
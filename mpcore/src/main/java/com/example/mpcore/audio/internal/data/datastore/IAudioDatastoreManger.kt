package com.example.mpcore.audio.internal.data.datastore

import com.example.mpcore.audio.api.data.model.RepeatMode

/**
 * This act as the las layer for the datastore so any data will be taken for data store by the sdk will be from this
 */
internal interface IAudioDatastoreManger {

    fun getIsDatabaseSynchronizedWithContentProvider(): IAudioDataStoreController<Boolean>

    fun getIsSdkInitialized(): IAudioDataStoreController<Boolean>

    fun getLastPlayingSongId(): IAudioDataStoreController<Long>

    fun getAudioProgression(): IAudioDataStoreController<Int>

    fun getIsInRandomMode(): IAudioDataStoreController<Boolean>

    fun getRepeatMode(): IAudioDataStoreController<@RepeatMode Int>

    fun getDatabaseVersionController(): IAudioDataStoreController<Int>
}
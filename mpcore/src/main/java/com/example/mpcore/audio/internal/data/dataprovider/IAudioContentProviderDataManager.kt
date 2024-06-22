package com.example.mpcore.audio.internal.data.dataprovider

import com.example.mpcore.audio.internal.data.dataprovider.model.AudioDataProviderModel

/**
 * This is abstraction to manage audio content provider
 * This is the final representation of audio internal storage
 */
internal interface IAudioContentProviderDataManager {

    /**
     * Call this to get list of audio from internal storage
     */
    suspend fun getAllAudio(): List<AudioDataProviderModel>

    /**
     * Call this to get listen for data changes in the devise internal
     * storage
     */
    fun setOnDataChangesListener(onDataChanges: ()->Unit)
}
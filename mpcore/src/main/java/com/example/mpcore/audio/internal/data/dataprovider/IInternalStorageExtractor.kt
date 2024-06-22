package com.example.mpcore.audio.internal.data.dataprovider

import com.example.mpcore.audio.internal.data.dataprovider.model.AudioDataProviderModel

/**
 * This abstraction of extracting all audio from device storage in
 *
 */
internal interface IInternalStorageExtractor {

    /**
     * Fetch all audio from device storage
     * @return [AudioDataProviderModel] list
     */
    suspend fun getAllAudioInStorage(): List<AudioDataProviderModel>


    /**
     * Listen to changes in internal storage data changes for audio
     * @param onDataChanges: this lambda will invoked when audio data changes
     * (new audio added or deleted ...) in the internal storage
     */
    fun setOnDataChangesListener(onDataChanges: ()->Unit)
}
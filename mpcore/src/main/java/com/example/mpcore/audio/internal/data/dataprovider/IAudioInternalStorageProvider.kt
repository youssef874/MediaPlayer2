package com.example.mpcore.audio.internal.data.dataprovider

import com.example.mpcore.audio.internal.data.dataprovider.model.AudioDataProviderModel

/**
 * Abstraction for providing audio data from internal storage
 */
internal interface IAudioInternalStorageProvider {

    /**
     * Call this to return method to all audio from device internal storage
     * if you allowed to.
     * @param audioInternalStorageValidator: [IAudioInternalStorageValidator] to validate internal storage access
     * @return [AudioDataProviderModel] list
     * @throws [MissingPermissionException] if there any permission needed is missing
     */
    suspend fun getAllAudio(audioInternalStorageValidator: IAudioInternalStorageValidator): List<AudioDataProviderModel>

    /**
     * Listen to changes in internal storage data changes for audio
     * @param onDataChanges: this lambda will invoked when audio data changes
     * (new audio added or deleted ...) in the internal storage
     * @param audioInternalStorageValidator: [IAudioInternalStorageValidator] to validate internal storage access
     * @throws [MissingPermissionException] if there any permission needed is missing
     */
    fun setOnDataChangesListener(audioInternalStorageValidator: IAudioInternalStorageValidator,onDataChanges: ()->Unit)
}
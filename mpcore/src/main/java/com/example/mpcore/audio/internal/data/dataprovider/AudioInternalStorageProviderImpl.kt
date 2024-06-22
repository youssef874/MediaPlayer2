package com.example.mpcore.audio.internal.data.dataprovider

import com.example.mpcore.audio.api.exception.MissingPermissionException
import com.example.mpcore.audio.internal.data.dataprovider.model.AudioDataProviderModel
import com.example.mpcore.logger.api.data.MPLoggerLevel
import com.example.mpcore.logger.internal.MPLoggerConfiguration

internal class AudioInternalStorageProviderImpl(
    private val internalStorageExtractor: IInternalStorageExtractor
): IAudioInternalStorageProvider {
    
    companion object{
        private const val CLASS_NAME = "AudioInternalStorageProviderImpl"
        private const val TAG = "CONTENT_PROVIDER"
    }
    
    override suspend fun getAllAudio(audioInternalStorageValidator: IAudioInternalStorageValidator): List<AudioDataProviderModel> {
        MPLoggerConfiguration.DefaultBuilder().log(
            CLASS_NAME,
            TAG,
            "getAllAudio",
            "start loading all audio",
            MPLoggerLevel.INFO
        )
        val permissionNeeded = audioInternalStorageValidator.permissionRequired()
        if (permissionNeeded.isNotEmpty()){
            MPLoggerConfiguration.DefaultBuilder().log(
                CLASS_NAME,
                TAG,
                "getAllAudio",
                "Missing permission: $permissionNeeded",
                MPLoggerLevel.ERROR)
            throw MissingPermissionException(permissionNeeded)
        }
        return internalStorageExtractor.getAllAudioInStorage()
    }

    override fun setOnDataChangesListener(
        audioInternalStorageValidator: IAudioInternalStorageValidator,
        onDataChanges: () -> Unit
    ) {
        val permissionNeeded = audioInternalStorageValidator.permissionRequired()
        if (permissionNeeded.isNotEmpty()){
            MPLoggerConfiguration.DefaultBuilder().log(
                CLASS_NAME,
                TAG,
                "setOnDataChangesListener",
                "Missing permission: $permissionNeeded",
                MPLoggerLevel.ERROR
            )
            throw MissingPermissionException(permissionNeeded)
        }
        internalStorageExtractor.setOnDataChangesListener(onDataChanges)
    }
}
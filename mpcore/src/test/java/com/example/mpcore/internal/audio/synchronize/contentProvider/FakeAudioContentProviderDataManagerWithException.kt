package com.example.mpcore.internal.audio.synchronize.contentProvider

import com.example.mpcore.audio.api.exception.MissingPermissionException
import com.example.mpcore.audio.internal.data.dataprovider.IAudioContentProviderDataManager
import com.example.mpcore.audio.internal.data.dataprovider.model.AudioDataProviderModel

internal object FakeAudioContentProviderDataManagerWithException: IAudioContentProviderDataManager {
    override suspend fun getAllAudio(): List<AudioDataProviderModel> {
        throw MissingPermissionException(listOf("READ_AUDIO"))
    }

    override fun setOnDataChangesListener(onDataChanges: () -> Unit) {
        //TODO("Not yet implemented")
    }
}
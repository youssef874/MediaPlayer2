package com.example.mpcore.internal.audio.synchronize.contentProvider

import com.example.mpcore.audio.internal.data.dataprovider.IAudioContentProviderDataManager
import com.example.mpcore.audio.internal.data.dataprovider.model.AudioDataProviderModel

internal object FakeEmptyAudioContentProvider: IAudioContentProviderDataManager {
    override suspend fun getAllAudio(): List<AudioDataProviderModel> {
        return emptyList()
    }

    override fun setOnDataChangesListener(onDataChanges: () -> Unit) {
        //TODO("Not yet implemented")
    }
}
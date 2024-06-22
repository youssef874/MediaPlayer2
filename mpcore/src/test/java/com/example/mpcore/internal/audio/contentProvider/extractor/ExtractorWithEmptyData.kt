package com.example.mpcore.internal.audio.contentProvider.extractor

import com.example.mpcore.audio.internal.data.dataprovider.IInternalStorageExtractor
import com.example.mpcore.audio.internal.data.dataprovider.model.AudioDataProviderModel

internal object ExtractorWithEmptyData: IInternalStorageExtractor {
    override suspend fun getAllAudioInStorage(): List<AudioDataProviderModel> {
        return emptyList()
    }

    override fun setOnDataChangesListener(onDataChanges: () -> Unit) {
        //TODO("Not yet implemented")
    }
}
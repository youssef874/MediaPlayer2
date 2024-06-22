package com.example.mpcore.internal.audio.contentProvider.extractor

import android.net.Uri
import com.example.mpcore.audio.internal.data.dataprovider.IInternalStorageExtractor
import com.example.mpcore.audio.internal.data.dataprovider.model.AudioDataProviderModel

internal object ExtractorWithNoEmptyData: IInternalStorageExtractor {

    val allAudio = listOf(
        AudioDataProviderModel(
            audioId = 1,
            artistName = "artist1",
            album = "album1",
            audioDuration = 1000,
            audioName = "name1",
            audioSize = 50,
            audioUri = Uri.parse("teqt1")
        ),
        AudioDataProviderModel(
            audioId = 2,
            artistName = "artist2",
            album = "album2",
            audioDuration = 1500,
            audioName = "name2",
            audioSize = 80,
            audioUri = Uri.parse("teqt2")
        )
    )

    override suspend fun getAllAudioInStorage(): List<AudioDataProviderModel> {
        return allAudio
    }

    override fun setOnDataChangesListener(onDataChanges: () -> Unit) {
        onDataChanges()
    }
}
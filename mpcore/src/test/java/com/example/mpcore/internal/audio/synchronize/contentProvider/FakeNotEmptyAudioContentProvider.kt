package com.example.mpcore.internal.audio.synchronize.contentProvider

import android.net.Uri
import com.example.mpcore.audio.internal.data.dataprovider.IAudioContentProviderDataManager
import com.example.mpcore.audio.internal.data.dataprovider.model.AudioDataProviderModel

internal object FakeNotEmptyAudioContentProvider: IAudioContentProviderDataManager {
    val list = mutableListOf(
        AudioDataProviderModel(
            audioId = 1,
            audioName = "name1",
            audioSize = 2000,
            album = "album1",
            artistName = "artist1",
            audioDuration = 500
        ),
        AudioDataProviderModel(
            audioId = 2,
            audioName = "name2",
            audioSize = 2000,
            album = "album2",
            artistName = "artist2",
            audioDuration = 500
        )
    )
    override suspend fun getAllAudio(): List<AudioDataProviderModel> {
        return list
    }

    private var onDataChanges: (()->Unit)? = null

    override fun setOnDataChangesListener(onDataChanges: () -> Unit) {
        this.onDataChanges = onDataChanges
    }

    fun changeData(){
        list.add(AudioDataProviderModel(
            audioId = 3,
            audioName = "name3",
            audioSize = 2500,
            album = "album3",
            artistName = "artist3",
            audioDuration = 1500
        ))
        onDataChanges?.invoke()
    }
}
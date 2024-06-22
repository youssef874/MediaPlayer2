package com.example.mpcore.audio.internal.mediaplayer.factory

import android.content.Context
import com.example.mpcore.audio.internal.data.datastore.factory.AudioDatastoreDataManagerFactory
import com.example.mpcore.audio.internal.mediaplayer.AudioPlayerMangerImpl
import com.example.mpcore.audio.internal.mediaplayer.IAudioPlayerManger

internal object AudioPlayerMangerFactory {

    fun create(context: Context): IAudioPlayerManger{
        return AudioPlayerMangerImpl(
            mediaPlayer = MediaPlayerFactory.create(),
            audioDataStoreManger = AudioDatastoreDataManagerFactory.create(context)
        )
    }
}
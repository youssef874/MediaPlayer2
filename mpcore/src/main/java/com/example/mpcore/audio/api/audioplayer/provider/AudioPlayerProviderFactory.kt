package com.example.mpcore.audio.api.audioplayer.provider

import android.content.Context
import com.example.mpcore.audio.internal.mediaplayer.InternalAudioManagerProviderImpl
import com.example.mpcore.audio.internal.mediaplayer.factory.AudioPlayerMangerFactory

internal object AudioPlayerProviderFactory {

    fun create(context: Context): IAudioPlayerProvider {
        return AudioPlayerProviderImpl(
            InternalAudioManagerProviderImpl(AudioPlayerMangerFactory.create(context)),context
        )
    }
}
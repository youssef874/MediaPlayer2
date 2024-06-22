package com.example.mpcore.audio.internal.mediaplayer.factory

import com.example.mpcore.audio.internal.mediaplayer.IMediaPlayer
import com.example.mpcore.audio.internal.mediaplayer.MediaPlayerImpl

internal object MediaPlayerFactory {

    @Volatile
    private var mediaPlayer: IMediaPlayer? = null

    fun create(): IMediaPlayer {
        return mediaPlayer ?: synchronized(this){
            val instance = MediaPlayerImpl
            instance
        }
    }
}
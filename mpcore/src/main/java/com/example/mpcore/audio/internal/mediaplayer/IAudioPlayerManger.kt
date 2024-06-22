package com.example.mpcore.audio.internal.mediaplayer

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.flow.Flow

internal interface IAudioPlayerManger {

    val isPlaying: Boolean

    fun playAudio(context: Context, uri: Uri, playAt: Int = -1): Boolean

    fun stopPlayingAudio(context: Context): Boolean

    fun resumePlayingAudio(context: Context): Boolean

    fun pausePlayingAudio(context: Context): Boolean

    fun observeAudioProgress(): Flow<Int>

    suspend fun getAudioProgress(): Int

    fun listenToAudioCompletion(onComplete: ()->Unit)

    fun updateCurrentPlayerPosition(position: Int): Boolean
}
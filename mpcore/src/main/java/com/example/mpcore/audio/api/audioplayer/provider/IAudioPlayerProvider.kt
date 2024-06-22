package com.example.mpcore.audio.api.audioplayer.provider

import android.net.Uri
import kotlinx.coroutines.flow.Flow

internal interface IAudioPlayerProvider {

    val isPlaying: Boolean

    fun playAudio(uri: Uri,playAt: Int = -1): Boolean

    fun stopPlayingAudio(): Boolean

    fun resumePlayingAudio(): Boolean

    fun pausePlayingAudio(): Boolean

    fun observeAudioProgress(): Flow<Int>

    suspend fun getAudioProgress(): Int

    fun listenToAudioCompletion(onComplete: () -> Unit)

    fun updateCurrentPlayerPosition(position: Int): Boolean
}
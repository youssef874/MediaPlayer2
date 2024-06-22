package com.example.mpcore.audio.internal.mediaplayer

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.flow.Flow

internal class InternalAudioManagerProviderImpl(
    private val audioPlayerManger: IAudioPlayerManger
): IInternalAudioManagerProvider {


    override val isPlaying: Boolean
        get() = audioPlayerManger.isPlaying


    override fun playAudio(context: Context, uri: Uri, playAt: Int): Boolean {
        return audioPlayerManger.playAudio(context, uri,playAt)
    }

    override fun stopPlayingAudio(context: Context): Boolean {
        return audioPlayerManger.stopPlayingAudio(context)
    }

    override fun resumePlayingAudio(context: Context): Boolean {
        return audioPlayerManger.resumePlayingAudio(context)
    }

    override fun pausePlayingAudio(context: Context): Boolean {
        return audioPlayerManger.pausePlayingAudio(context)
    }

    override fun observeAudioProgress(): Flow<Int> {
        return audioPlayerManger.observeAudioProgress()
    }

    override suspend fun getAudioProgress(): Int {
       return audioPlayerManger.getAudioProgress()
    }

    override fun listenToAudioCompletion(onComplete: () -> Unit) {
        audioPlayerManger.listenToAudioCompletion(onComplete)
    }

    override fun updateCurrentPlayerPosition(position: Int): Boolean {
        return audioPlayerManger.updateCurrentPlayerPosition(position)
    }


}
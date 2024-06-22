package com.example.mpcore.audio.api.audioplayer.provider

import android.content.Context
import android.net.Uri
import com.example.mpcore.audio.internal.mediaplayer.IInternalAudioManagerProvider
import kotlinx.coroutines.flow.Flow

/**
 * This class represent as intermediate between api and internal fo audio manipulation
 */
internal class AudioPlayerProviderImpl(
    private val internalAudioManagerProvider: IInternalAudioManagerProvider,
    private val context: Context
): IAudioPlayerProvider {


    override val isPlaying: Boolean
        get() = internalAudioManagerProvider.isPlaying

    override fun playAudio(uri: Uri, playAt: Int): Boolean {
        return internalAudioManagerProvider.playAudio(context, uri,playAt)
    }

    override fun stopPlayingAudio(): Boolean {
        return internalAudioManagerProvider.stopPlayingAudio(context)
    }

    override fun resumePlayingAudio(): Boolean {
        return internalAudioManagerProvider.resumePlayingAudio(context)
    }

    override fun pausePlayingAudio(): Boolean {
        return internalAudioManagerProvider.pausePlayingAudio(context)
    }

    override fun observeAudioProgress(): Flow<Int> {
        return internalAudioManagerProvider.observeAudioProgress()
    }

    override suspend fun getAudioProgress(): Int {
        return internalAudioManagerProvider.getAudioProgress()
    }

    override fun listenToAudioCompletion(onComplete: () -> Unit) {
        internalAudioManagerProvider.listenToAudioCompletion(onComplete)
    }

    override fun updateCurrentPlayerPosition(position: Int): Boolean {
        return internalAudioManagerProvider.updateCurrentPlayerPosition(position)
    }
}
package com.example.mpcore.audio.internal.mediaplayer

import android.content.Context
import android.net.Uri
import com.example.mpcore.audio.internal.data.datastore.IAudioDatastoreManger
import com.example.mpcore.common.internal.SDKComponent
import com.example.mpcore.logger.api.data.MPLoggerLevel
import com.example.mpcore.logger.internal.MPLoggerConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

internal class AudioPlayerMangerImpl(
    private val mediaPlayer: IMediaPlayer,
    private val audioDataStoreManger: IAudioDatastoreManger
): IAudioPlayerManger {

    companion object{
        private const val CLASS_NAME = "AudioPlayerMangerImpl"
        private const val TAG = "AUDIO_PLAYER"
    }

    private var audioProgressJob: Job? = null

    init {
        mediaPlayer.onAudioProgressionListener {
            audioProgressJob?.cancel()
            audioProgressJob = null
            audioProgressJob = SDKComponent.CoroutineComponent.sdkCoroutineScope.launch(Dispatchers.IO) {
                audioDataStoreManger.getAudioProgression().updateValue(it)
            }
        }
    }

    override val isPlaying: Boolean
        get() = mediaPlayer.isPlaying

    override fun playAudio(context: Context, uri: Uri, playAt: Int): Boolean {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME, tag = TAG, methodName = "playAudio",
            msg = "play $uri", logLevel = MPLoggerLevel.INFO
        )
        return mediaPlayer.playAudio(context, uri,playAt)
    }

    override fun stopPlayingAudio(context: Context): Boolean {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME, tag = TAG, methodName = "stopPlayingAudio",
            msg = "stop current playing audio", logLevel = MPLoggerLevel.INFO
        )
        return mediaPlayer.stopPlayingAudio(context)
    }

    override fun resumePlayingAudio(context: Context): Boolean {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME, tag = TAG, methodName = "resumePlayingAudio",
            msg = "resume current audio", logLevel = MPLoggerLevel.INFO
        )
        return mediaPlayer.resumePlyingAudio(context)
    }

    override fun pausePlayingAudio(context: Context): Boolean {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME, tag = TAG, methodName = "pausePlayingAudio",
            msg = "pause current playing audio", logLevel = MPLoggerLevel.INFO
        )
        return mediaPlayer.pausePlayer(context)
    }

    override fun observeAudioProgress(): Flow<Int> {
        return audioDataStoreManger.getAudioProgression().observeValue()
    }

    override suspend fun getAudioProgress(): Int {
        return audioDataStoreManger.getAudioProgression().getValue()
    }

    override fun listenToAudioCompletion(onComplete: () -> Unit) {
        mediaPlayer.setOnAudioCompletedListener(onComplete)
    }

    override fun updateCurrentPlayerPosition(position: Int): Boolean {
        return mediaPlayer.updateCurrentPlayerPosition(position)
    }
}
package com.example.mpcore.audio.internal.mediaplayer

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import com.example.mpcore.common.internal.SDKComponent
import com.example.mpcore.logger.api.data.MPLoggerLevel
import com.example.mpcore.logger.internal.MPLoggerConfiguration
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal object MediaPlayerImpl : IMediaPlayer {

    private const val CLASS_NAME = "MediaPlayerImpl"
    private const val TAG = "MEDIA_PLAYER"

    private var mediaPlayer: MediaPlayer? = null
    private var onSongCompleted: (() -> Unit)? = null
    private var onAudioProgress: ((Int) -> Unit)? = null
    private var progressJob: Job? = null
    private var currentUri: Uri? = null

    private fun startListenToSongCompletion() {
        mediaPlayer?.setOnCompletionListener {
            MPLoggerConfiguration.DefaultBuilder().log(
                className = CLASS_NAME,
                tag = TAG,
                methodName = "startListenToSongCompletion",
                msg = "audioSessionId: ${it.audioSessionId}",
                logLevel = MPLoggerLevel.INFO
            )
            mediaPlayer = null
            onSongCompleted?.invoke()
        }
    }

    private fun startListenForProgress() {
        progressJob = SDKComponent.CoroutineComponent.sdkCoroutineScope.launch {
            while (mediaPlayer != null && mediaPlayer?.isPlaying == true) {
                mediaPlayer?.currentPosition?.let {
                    onAudioProgress?.invoke(it)
                    delay(1000)
                }
            }
        }
    }

    private fun stopListeningToAudioProgress(){
        progressJob?.cancel()
        progressJob = null
    }

    override val isPlaying: Boolean
        get() = mediaPlayer?.isPlaying == true

    override fun playAudio(context: Context, uri: Uri, playAt: Int): Boolean {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME, tag = TAG,
            methodName = "playAudio", msg = "trying to play: $uri", logLevel = MPLoggerLevel.INFO
        )
        try {
            val player = getPlayerToPlay(context, uri)
            if (mediaPlayer?.isPlaying == true) {
                if (currentUri != uri) {
                    MPLoggerConfiguration.DefaultBuilder().log(
                        className = CLASS_NAME,
                        tag = TAG,
                        methodName = "playAudio",
                        msg = "request playing other audio file while the current is still playing so stop the current player first",
                        logLevel = MPLoggerLevel.INFO
                    )
                    stopPlayingAudio(context)
                } else {
                    MPLoggerConfiguration.DefaultBuilder().log(
                        className = CLASS_NAME,
                        tag = TAG,
                        methodName = "playAudio",
                        msg = "Song already playing cannot replay it",
                        logLevel = MPLoggerLevel.INFO
                    )
                    return true
                }
            }
            if (!player.isPlaying) {
                mediaPlayer = player
                currentUri = uri
                if (playAt != -1){
                    player.seekTo(playAt)
                }
                player.start()
            } else {
                MPLoggerConfiguration.DefaultBuilder().log(
                    className = CLASS_NAME,
                    tag = TAG,
                    methodName = "playAudio",
                    msg = "Song already playing cannot replay it",
                    logLevel = MPLoggerLevel.WARNING
                )
            }
            startListenToSongCompletion()
            startListenForProgress()
            return player.isPlaying
        } catch (e: IllegalStateException) {
            MPLoggerConfiguration.DefaultBuilder().log(
                className = CLASS_NAME,
                tag = TAG,
                methodName = "playAudio",
                msg = "failed to play: $uri, message: ${e.message}",
                logLevel = MPLoggerLevel.ERROR
            )
            return false
        }
    }

    private fun getPlayerToPlay(context: Context, uri: Uri): MediaPlayer {
        val mediaPlayer = MediaPlayer()
        mediaPlayer.apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(context, uri)
            prepare()
        }
        return mediaPlayer
    }

    override fun stopPlayingAudio(context: Context): Boolean {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME,
            tag = TAG,
            methodName = "stopPlayingAudio",
            msg = "try to stop current playing audio",
            logLevel = MPLoggerLevel.INFO
        )
        mediaPlayer?.let {
            try {
                if (it.isPlaying) {
                    MPLoggerConfiguration.DefaultBuilder().log(
                        className = CLASS_NAME,
                        tag = TAG,
                        methodName = "stopPlayingAudio",
                        msg = "stop the player",
                        logLevel = MPLoggerLevel.DEBUG
                    )
                    stopListeningToAudioProgress()
                    it.stop()
                    it.release()
                } else {
                    MPLoggerConfiguration.DefaultBuilder().log(
                        className = CLASS_NAME,
                        tag = TAG,
                        methodName = "stopPlayingAudio",
                        msg = "the player was not playing so there no need ro stop it",
                        logLevel = MPLoggerLevel.DEBUG
                    )
                }
                return !it.isPlaying
            } catch (e: IllegalStateException) {
                MPLoggerConfiguration.DefaultBuilder().log(
                    className = CLASS_NAME,
                    tag = TAG,
                    methodName = "stopPlayingAudio",
                    msg = "failed to stop player, message: ${e.message}",
                    logLevel = MPLoggerLevel.ERROR
                )
                return false
            }
        } ?: run {
            MPLoggerConfiguration.DefaultBuilder().log(
                className = CLASS_NAME,
                tag = TAG,
                methodName = "stopPlayingAudio",
                msg = "you did not play any audio yet so there nothing to stop",
                logLevel = MPLoggerLevel.WARNING
            )
            return true
        }
    }

    override fun resumePlyingAudio(context: Context): Boolean {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME,
            tag = TAG,
            methodName = "resumePlyingAudio",
            msg = "try to resume current playing audio",
            logLevel = MPLoggerLevel.INFO
        )
        mediaPlayer?.let {
            try {
                if (!it.isPlaying) {
                    MPLoggerConfiguration.DefaultBuilder().log(
                        className = CLASS_NAME,
                        tag = TAG,
                        methodName = "resumePlyingAudio",
                        msg = "resume the current playing audio",
                        logLevel = MPLoggerLevel.INFO
                    )
                    it.start()
                } else {
                    MPLoggerConfiguration.DefaultBuilder().log(
                        className = CLASS_NAME,
                        tag = TAG,
                        methodName = "resumePlyingAudio",
                        msg = "the player is already playing , no need to resume",
                        logLevel = MPLoggerLevel.INFO
                    )
                }
                startListenForProgress()
                return it.isPlaying
            } catch (e: IllegalStateException) {
                MPLoggerConfiguration.DefaultBuilder().log(
                    className = CLASS_NAME,
                    tag = TAG,
                    methodName = "resumePlyingAudio",
                    msg = "failed to resume player, message: ${e.message}",
                    logLevel = MPLoggerLevel.ERROR
                )
                return false
            }
        } ?: run {
            MPLoggerConfiguration.DefaultBuilder().log(
                className = CLASS_NAME,
                tag = TAG,
                methodName = "resumePlyingAudio",
                msg = "you did not play any audio yet so there nothing to resume",
                logLevel = MPLoggerLevel.WARNING
            )
            return false
        }
    }

    override fun pausePlayer(context: Context): Boolean {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME,
            tag = TAG,
            methodName = "pausePlayer",
            msg = "try pause the current playing audio",
            logLevel = MPLoggerLevel.INFO
        )
        mediaPlayer?.let {
            try {
                if (it.isPlaying) {
                    MPLoggerConfiguration.DefaultBuilder().log(
                        className = CLASS_NAME,
                        tag = TAG,
                        methodName = "pausePlayer",
                        msg = "pause the current playing audio",
                        logLevel = MPLoggerLevel.INFO
                    )
                    it.pause()
                } else {
                    MPLoggerConfiguration.DefaultBuilder().log(
                        className = CLASS_NAME,
                        tag = TAG,
                        methodName = "pausePlayer",
                        msg = "the player is already not playing , no need to pause",
                        logLevel = MPLoggerLevel.INFO
                    )
                }
                stopListeningToAudioProgress()
                return !it.isPlaying
            } catch (e: IllegalStateException) {
                MPLoggerConfiguration.DefaultBuilder().log(
                    className = CLASS_NAME,
                    tag = TAG,
                    methodName = "pausePlayer",
                    msg = "failed to pause player, message: ${e.message}",
                    logLevel = MPLoggerLevel.ERROR
                )
                return false
            }
        } ?: run {
            MPLoggerConfiguration.DefaultBuilder().log(
                className = CLASS_NAME,
                tag = TAG,
                methodName = "pausePlayer",
                msg = "you did not play any audio yet so there nothing to pause",
                logLevel = MPLoggerLevel.WARNING
            )
            return true
        }
    }

    override fun setOnAudioCompletedListener(onAudioCompleted: () -> Unit) {
        this.onSongCompleted = onAudioCompleted
    }

    override fun onAudioProgressionListener(onAudioProgress: (Int) -> Unit) {
        this.onAudioProgress = onAudioProgress
    }

    override fun unregisterToAudioProgress() {
        onAudioProgress = null
    }

    override fun getCurrentProgress(): Int {
        return mediaPlayer?.currentPosition ?: -1
    }

    override fun updateCurrentPlayerPosition(position: Int): Boolean {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME,
            tag = TAG,
            methodName = "updatePlayerPosition",
            msg = "try to update player position position: $position",
            logLevel = MPLoggerLevel.DEBUG
        )
        mediaPlayer?.let {
            if (position > -1){
                it.seekTo(position)
            }
            return true
        }?:run {
            MPLoggerConfiguration.DefaultBuilder().log(
                className = CLASS_NAME,
                tag = TAG,
                methodName = "updatePlayerPosition",
                msg = "player is null could update player position",
                logLevel = MPLoggerLevel.WARNING
            )
            return false
        }
    }
}
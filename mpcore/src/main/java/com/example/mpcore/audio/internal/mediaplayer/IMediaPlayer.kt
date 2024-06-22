package com.example.mpcore.audio.internal.mediaplayer

import android.content.Context
import android.net.Uri

internal interface IMediaPlayer {

    val isPlaying: Boolean

    /**
     * Play the provided uri
     * @param uri: the [Uri] that provide the audio file to read
     * @return true if the audio played otherwise it will return false
     */
    fun playAudio(context: Context, uri: Uri,playAt: Int = -1): Boolean

    /**
     * Stop playing the current playing song
     * @return true if the audio stopped otherwise it will return false
     */
    fun stopPlayingAudio(context: Context): Boolean

    /**
     * resume playing the current song
     * @return true if the audio resumed otherwise it will return false
     */
    fun resumePlyingAudio(context: Context): Boolean

    /**
     * pause playing the current playing song
     * @return true if the audio paused otherwise it will return false
     */
    fun pausePlayer(context: Context): Boolean

    /**
     * Listen to audio completion
     */
    fun setOnAudioCompletedListener(onAudioCompleted: ()->Unit)

    /**
     * Listen to audio progress
     * @param onAudioProgress: callback to return the progress in int
     */
    fun onAudioProgressionListener(onAudioProgress: (Int)->Unit)

    /**
     * Unregister to audio completion listener
     */
    fun unregisterToAudioProgress()

    /**
     * Get the audio file current position it will return -1 if there no audio file requested to play yet
     */
    fun getCurrentProgress(): Int

    /**
     * Update player position. the player will be playing from the requested position
     * @param position: which the player will be starting at
     * @return true if the update was successful otherwise return false
     */
    fun updateCurrentPlayerPosition(position: Int): Boolean
}
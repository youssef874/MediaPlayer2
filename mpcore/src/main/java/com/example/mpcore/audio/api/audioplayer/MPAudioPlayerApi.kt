package com.example.mpcore.audio.api.audioplayer

import android.net.Uri
import com.example.mpcore.audio.api.audioplayer.provider.AudioPlayerProviderFactory
import com.example.mpcore.common.internal.SDKComponent
import kotlinx.coroutines.flow.Flow

/**
 * This class to manipulate audio file in the storage
 */
object MPAudioPlayerApi {

    private val audioPlayerProvider = AudioPlayerProviderFactory.create(SDKComponent.SDKContext.getContext())

    /**
     * Call this to check if there is audio currently playing
     */

    val isPlaying : Boolean get() = audioPlayerProvider.isPlaying

    /**
     * Call this to play the audio file
     * @param uri: Android uri which will lead to the audio file
     * @return true if the play is successful otherwise it will return false
     */
    fun playAudio(uri: Uri,playAt: Int = -1): Boolean {
        return audioPlayerProvider.playAudio(uri,playAt)
    }

    /**
     * Call this to stop the current playing song
     * @return true if the stop is successful otherwise it will return false
     */
    fun stopPlayingAudio(): Boolean {
        return audioPlayerProvider.stopPlayingAudio()
    }

    /**
     * Call this to resume the current song
     * @return true if the resume is successful otherwise it will return false
     */
    fun resumePlayingAudio(): Boolean {
        return audioPlayerProvider.resumePlayingAudio()
    }

    /**
     * Call this to pause the current playing song
     * @return true if the pause is successful otherwise it will return false
     */
    fun pausePlayingAudio(): Boolean {
        return audioPlayerProvider.pausePlayingAudio()
    }

    /**
     * Observe the current playing audio file progress
     */
    fun observeAudioProgress(): Flow<Int> {
       return audioPlayerProvider.observeAudioProgress()
    }

    /**
     * Get the current playing song position or the last
     * progress of last played song
     */
    suspend fun getAudioProgress(): Int {
        return audioPlayerProvider.getAudioProgress()
    }

    /**
     * Call this to listen to song completion
     */
    fun listenToAudioCompletion(onComplete: () -> Unit) {
        audioPlayerProvider.listenToAudioCompletion(onComplete)
    }

    /**
     * Update player position. the player will be playing from the requested position
     * @param position: which the player will be starting at
     * @return true if the update was successful otherwise return false
     */
    fun updateCurrentPlayerPosition(position: Int): Boolean{
        return audioPlayerProvider.updateCurrentPlayerPosition(position)
    }
}
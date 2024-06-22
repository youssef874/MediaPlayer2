package com.example.mediaplayer.domain

import com.example.mediaplayer.domain.model.UiAudio
import kotlinx.coroutines.flow.Flow

interface IPlayOrStopAudioUseCase {

    val currentAudio: UiAudio?

    val isPlaying: Boolean

    suspend fun playAudio(uiAudio: UiAudio, playAt: Int = -1)

    suspend fun stopCurrentPlayingSong()

    suspend fun resumeCurrentSong(uiAudio: UiAudio)

    suspend fun pauseAudio()

    fun updatePlayerPosition(position: Int)

    suspend fun onAudioPlayedChangeListener(onAudioPlayChanges: (UiAudio, Boolean)->Unit, predicate: ()->Boolean)

    suspend fun getAudioProgress(): Int

    fun observeAudioProgress(): Flow<Int>
}
package com.example.mediaplayer.domain

import com.example.mediaplayer.domain.model.UiAudio

interface INextOrPreviousUseCase {

    suspend fun playNext(currentSong: UiAudio): UiAudio

    suspend fun playPrevious(currentSong: UiAudio): UiAudio
}
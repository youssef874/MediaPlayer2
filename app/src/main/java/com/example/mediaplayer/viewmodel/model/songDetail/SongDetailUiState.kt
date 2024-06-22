package com.example.mediaplayer.viewmodel.model.songDetail

import com.example.mediaplayer.data.model.RepeatMode
import com.example.mediaplayer.domain.model.UiAudio

data class SongDetailUiState(
    val currentSong: UiAudio = UiAudio(),
    val isPlaying: Boolean = false,
    val progress: Int = 0,
    val isRandom: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.NO_REPEAT
)

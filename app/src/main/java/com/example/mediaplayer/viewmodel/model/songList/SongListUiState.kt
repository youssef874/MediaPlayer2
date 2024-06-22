package com.example.mediaplayer.viewmodel.model.songList

import com.example.mediaplayer.domain.model.UiAudio

data class SongListUiState(
    val isLoading: Boolean = true,
    val dataList: List<UiAudio> = emptyList(),
    val isError: Boolean = false,
    val isPermissionDenied: Boolean = false,
    val page: Int = 0,
    val isNextItemLoading: Boolean = false,
    val isEndReached: Boolean = false,
    val currentSong: UiAudio? = null,
    val isPlaying: Boolean = false
)

package com.example.mediaplayer.viewmodel.model.songList

import com.example.mediaplayer.domain.model.UiAudio

sealed class SongListUiEvent {

    data object OnPermissionNotGranted: SongListUiEvent()

    data object OnPermissionGranted: SongListUiEvent()

    data object LoadData: SongListUiEvent()

    data object LoadNextItem: SongListUiEvent()

    data class PlaySelectedSong(val uiAudio: UiAudio): SongListUiEvent()

    data object PlayOrPauseCurrentSong: SongListUiEvent()

    data object PlayNextSong: SongListUiEvent()

    data object PlayPreviousSong: SongListUiEvent()
}
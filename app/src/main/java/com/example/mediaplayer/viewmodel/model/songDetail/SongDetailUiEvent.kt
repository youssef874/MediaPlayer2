package com.example.mediaplayer.viewmodel.model.songDetail

import com.example.mediaplayer.viewmodel.model.songList.SongListUiEvent

sealed class SongDetailUiEvent {

    data class Setup(val audioId: Long): SongDetailUiEvent()

    data object PlayOrPauseCurrentSong: SongDetailUiEvent()

    data object PlayNextSong: SongDetailUiEvent()

    data object PlayPreviousSong: SongDetailUiEvent()

    data object ChangeIsRandomMode: SongDetailUiEvent()

    data class SeekPlayerToPosition(val position: Int):SongDetailUiEvent()

    data object ChangeRepeatMode: SongDetailUiEvent()

    data object ChangeSongFavoriteStatus: SongDetailUiEvent()
}
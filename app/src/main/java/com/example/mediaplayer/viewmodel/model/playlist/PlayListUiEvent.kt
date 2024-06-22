package com.example.mediaplayer.viewmodel.model.playlist

import com.example.mediaplayer.domain.model.UiPlayList

sealed class PlayListUiEvent {

    data object LoadData: PlayListUiEvent()

    data class AttachSongToPlayList(val songId: Long,val uiPlayList: UiPlayList): PlayListUiEvent()

    data object LoadMoreData: PlayListUiEvent()

    data object AttachSongToPlayListResultHandled: PlayListUiEvent()
}
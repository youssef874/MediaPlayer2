package com.example.mediaplayer.viewmodel.model.playlist

import com.example.mediaplayer.domain.model.UiPlayList
import com.example.mediaplayer.viewmodel.model.UiResult

data class PlayListUIState(
    val dataList: List<UiPlayList> = emptyList(),
    val isLoading: Boolean = true,
    val isEndReached: Boolean = false,
    val isNextItemLoading: Boolean = false,
    val page: Int = 1,
    val attachToSongResult: UiResult<Unit>? = null
)

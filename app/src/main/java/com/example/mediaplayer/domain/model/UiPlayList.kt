package com.example.mediaplayer.domain.model

import android.net.Uri

data class UiPlayList(
    val playListId: Long = 0,
    val playListName: String = "",
    val thumbnailUri: Uri? = null
)

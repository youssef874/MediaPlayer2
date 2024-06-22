package com.example.mediaplayer.domain.model

import android.net.Uri

data class UiAudio(
    val id: Long = -1,
    val songName: String = "",
    val artist: String = "",
    val album: String = "",
    val uri: Uri = Uri.EMPTY,
    val size: Int = 0,
    val duration: Int = 0,
    val thumbnailUri: Uri?  = null,
    val isFavorite : Boolean = false
)

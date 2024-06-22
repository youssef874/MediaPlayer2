package com.example.mpcore.audio.api.data.model

import android.net.Uri

data class MPAudio(
    val id: Long,
    val artistName: String,
    val audioName: String,
    val album: String,
    val duration: Int,
    val size: Int,
    val uri: Uri,
    val songThumbnail: Uri?,
    val isFavorite: Boolean
)

package com.example.mpcore.audio.internal.data.dataprovider.model

import android.net.Uri

/**
 * Tis data class represent model of audio data from contentProvider
 */
internal data class AudioDataProviderModel(
    val audioId: Long = -1,
    val artistName: String = "",
    val album: String = "",
    val audioDuration: Int = 0,
    val audioUri: Uri = Uri.EMPTY,
    val audioThumbnailUri: Uri? = null,
    val audioName: String = "",
    val audioSize: Int = 0
)
